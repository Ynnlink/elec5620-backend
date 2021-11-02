package net.guides.springboot2.springboot2webappjsp.controllers;

import net.guides.springboot2.springboot2webappjsp.auth.JwtUtil;
import net.guides.springboot2.springboot2webappjsp.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import net.guides.springboot2.springboot2webappjsp.repositories.UserRepository;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("user")
public class UserProfileController {

	@Autowired
	UserRepository userRepo;

	@GetMapping("/user-profile")
	public Result getUserProfile(HttpServletRequest request) {

		String face_id = JwtUtil.getUserFaceIdByToken(request).getData().toString();

		User user = userRepo.findByUser_face_id(face_id);

		Result result = new Result();
		result.setData(user);

		return result;

	}

	@PutMapping("/user-profile")
	public Result editUserProfile(HttpServletRequest request) {
		return null;
	}






	

}
