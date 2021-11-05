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

	//default method
	@GetMapping
	public Result getUserProfile(HttpServletRequest request) {

		String face_id = JwtUtil.getUserFaceIdByToken(request).getData().toString();

		//find user or team in database
		User user = userRepo.findByUser_face_id(face_id);
		RescueTeam team = teamRepo.findByContacts_face_id(face_id);

		if (user != null) {
			return Result.succ("User(Admin) query success!", user);
		} else if (team != null) {
			return Result.succ("Team query success!", team);
		} else {
			return Result.fail("Query fail!");
		}
	}

	@PutMapping("/user")
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

	@PutMapping("/team")
	public Result editTeamProfile(HttpServletRequest request, @RequestBody RescueTeam updateTeam) {

		String face_id = JwtUtil.getUserFaceIdByToken(request).getData().toString();

		//find team in database
		RescueTeam team = teamRepo.findByContacts_face_id(face_id);

		//invalid condition
		if (StringUtils.isEmpty(updateTeam.getMobile_phone())) {
			return Result.fail("Contacts mobile phone can't be empty!");
		} else if (StringUtils.isEmpty(updateTeam.getAddress())) {
			return Result.fail("Address can't be empty!");
		} else if (StringUtils.isEmpty(updateTeam.getTeam_name())) {
			return Result.fail("Team name can't be empty!");
		} else if (StringUtils.isEmpty(updateTeam.getType())) {
			return Result.fail("Rescue type can't be empty!");
		} else if (updateTeam.getPersonnel_number() <= 0) {
			return Result.fail("Invalid personnel number!");
		} else {
			//update database

			team.setTeam_name(updateTeam.getTeam_name());
			team.setAddress(updateTeam.getAddress());
			team.setMobile_phone(updateTeam.getMobile_phone());
			team.setType(updateTeam.getType());
			team.setPersonnel_number(updateTeam.getPersonnel_number());

			teamRepo.save(team);

			RescueTeam temp = teamRepo.findByContacts_face_id(face_id);

			//validate data
			boolean condition = updateTeam.getTeam_name().equals(team.getTeam_name())
					&& updateTeam.getAddress().equals(temp.getAddress())
					&& updateTeam.getMobile_phone().equals(temp.getMobile_phone())
					&& updateTeam.getType().equals(temp.getType())
					&& updateTeam.getPersonnel_number() == team.getPersonnel_number();

			if (condition) {
				return Result.succ("Update success!");
			} else {
				return Result.fail("Update fail!");
			}
		}
	}
}
