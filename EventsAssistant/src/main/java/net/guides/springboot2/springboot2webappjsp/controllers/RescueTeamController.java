package net.guides.springboot2.springboot2webappjsp.controllers;


import net.guides.springboot2.springboot2webappjsp.authentication.JwtUtil;
import net.guides.springboot2.springboot2webappjsp.domain.Event;
import net.guides.springboot2.springboot2webappjsp.domain.RescueTeam;
import net.guides.springboot2.springboot2webappjsp.domain.User;
import net.guides.springboot2.springboot2webappjsp.repositories.EventRepository;
import net.guides.springboot2.springboot2webappjsp.repositories.RescueTeamRepository;
import net.guides.springboot2.springboot2webappjsp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/rescue")
public class RescueTeamController {

    @Autowired
    UserRepository userRepo;
    @Autowired
    RescueTeamRepository teamRepo;
    @Autowired
    EventRepository eventRepo;

    @PostMapping
    public Result takeoverEvent(HttpServletRequest request, @RequestParam(value = "event_id") String id) {
        //validate token
        Result result = JwtUtil.getUserFaceIdByToken(request);
        if (result.getCode().equals("-1")) {
            return Result.fail("Token expired!");
        }
        String face_id = result.getData().toString();

        return null;
    }

    @PostMapping("/complete")
    public Result completeEvent(HttpServletRequest request, @RequestParam(value = "event_id") String id, @RequestParam(value = "report") String report) {
        //validate token
        Result result = JwtUtil.getUserFaceIdByToken(request);
        if (result.getCode().equals("-1")) {
            return Result.fail("Token expired!");
        }
        String face_id = result.getData().toString();

        return null;
    }

    @PostMapping("/change_status")
    public Result changeStatus(HttpServletRequest request, @RequestParam(value = "status") String status) {
        //validate token
        Result result = JwtUtil.getUserFaceIdByToken(request);
        if (result.getCode().equals("-1")) {
            return Result.fail("Token expired!");
        }
        String face_id = result.getData().toString();

        return null;


    }


    //list all free teams (user)
    //list all teams (admin)
    @GetMapping
    public Result listAllTeam(HttpServletRequest request) {
        //validate token
        Result result = JwtUtil.getUserFaceIdByToken(request);
        if (result.getCode().equals("-1")) {
            return Result.fail("Token expired!");
        }
        String face_id = result.getData().toString();

        //find user and validate
        User user = userRepo.findByUser_face_id(face_id);

        if (user == null) {
            return Result.fail("No such user's information!");
        } else if (user.getType().equals("user")) {
            List<RescueTeam> freeTeam = teamRepo.findAllFreeTeam();
            if (freeTeam.isEmpty()) {
                return Result.fail("No free teams available!");
            } else {
                return Result.succ("Query success!", teamRepo.findAllFreeTeam());
            }
        } else {
            //admin
            List<RescueTeam> allTeam = teamRepo.findAllFreeTeam();
            if (allTeam.isEmpty()) {
                return Result.fail("No teams!");
            } else {
                return Result.succ("Query success!", allTeam);
            }
        }
    }





}
