package net.guides.springboot2.springboot2webappjsp.controllers;


import net.guides.springboot2.springboot2webappjsp.authentication.JwtUtil;
import net.guides.springboot2.springboot2webappjsp.repositories.EventRepository;
import net.guides.springboot2.springboot2webappjsp.repositories.RescueTeamRepository;
import net.guides.springboot2.springboot2webappjsp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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


    //list all free team
    @GetMapping
    public Result listAllTeam(HttpServletRequest request) {
        //validate token
        Result result = JwtUtil.getUserFaceIdByToken(request);
        if (result.getCode().equals("-1")) {
            return Result.fail("Token expired!");
        }
        String face_id = result.getData().toString();


        return null;
    }





}
