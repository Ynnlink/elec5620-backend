package net.guides.springboot2.springboot2webappjsp.controllers;

import net.guides.springboot2.springboot2webappjsp.authentication.JwtUtil;
import net.guides.springboot2.springboot2webappjsp.domain.RescueTeam;
import net.guides.springboot2.springboot2webappjsp.domain.User;
import net.guides.springboot2.springboot2webappjsp.repositories.RescueTeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import net.guides.springboot2.springboot2webappjsp.repositories.UserRepository;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("profile")
public class ProfileController {

	@Autowired
	UserRepository userRepo;
	@Autowired
	RescueTeamRepository teamRepo;

	@GetMapping("/get_info")
	public Result getUserProfile(HttpServletRequest request) {

		String face_id = JwtUtil.getUserFaceIdByToken(request).getData().toString();

		//find user or team in database
		User user = userRepo.findByUser_face_id(face_id);
		RescueTeam team = teamRepo.findByContacts_face_id(face_id);

		if (user != null) {
			return Result.succ("User/Admin query success!", user);
		} else if (team != null) {
			return Result.succ("Team query success!", team);
		} else {
			return Result.fail("Query fail!");
		}
	}

	@PutMapping("/user-profile")
	public Result editUserProfile(HttpServletRequest request, @RequestParam(value = "name") String name, @RequestParam(value = "phone") String phone) {

		String face_id = JwtUtil.getUserFaceIdByToken(request).getData().toString();

		//find user in database
		User user = userRepo.findByUser_face_id(face_id);

		if (user != null) {
			try {
				//invalid condition
				if (StringUtils.isEmpty(phone)) {
					return Result.fail("Mobile phone number can't be empty!");
				} else if (StringUtils.isEmpty(name)) {
					return Result.fail("Name can't be empty!");
				} else {
					user.setUser_name(name);
					user.setTelephone(phone);
					userRepo.save(user);
					User temp = userRepo.findByUser_face_id(face_id);
					if (temp.getUser_name().equals(name) && temp.getTelephone().equals(phone)) {
						return Result.succ("Update success!");
					} else {
						return Result.fail("Update fail!");
					}
				}
			} catch (NullPointerException npe) {
				return Result.fail("Null value exist!");
			}
		} else {
			return Result.fail("No such user!");
		}
	}

	@PutMapping("/team-profile")
	public Result editTeamProfile(HttpServletRequest request, @RequestBody RescueTeam team) {






		return null;
	}







}
