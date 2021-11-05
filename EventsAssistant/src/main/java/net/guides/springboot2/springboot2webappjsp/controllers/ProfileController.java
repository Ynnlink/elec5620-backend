package net.guides.springboot2.springboot2webappjsp.controllers;

import net.guides.springboot2.springboot2webappjsp.authentication.JwtUtil;
import net.guides.springboot2.springboot2webappjsp.domain.RescueTeam;
import net.guides.springboot2.springboot2webappjsp.domain.User;
import net.guides.springboot2.springboot2webappjsp.repositories.RescueTeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
	public Result editUserProfile(HttpServletRequest request) {
		return null;
	}






}
