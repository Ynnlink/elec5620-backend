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
@RequestMapping("/rescue_team")
public class RescueTeamController {

    @Autowired
    UserRepository userRepo;
    @Autowired
    RescueTeamRepository teamRepo;
    @Autowired
    EventRepository eventRepo;

    @PostMapping
    public Result takeoverEvent(HttpServletRequest request, @RequestParam(value = "event_id") int id) {
        //validate token
        Result result = JwtUtil.getUserFaceIdByToken(request);
        if (result.getCode().equals("-1")) {
            return Result.fail("Token expired!");
        }
        String face_id = result.getData().toString();
        RescueTeam team = teamRepo.findByContacts_face_id(face_id);
        Optional<Event> event = eventRepo.findById(id);
        //invalid condition
        if (team == null) {
            return Result.fail("No such team!");
        } else if (id <= 0) {
            return Result.fail("Invalid event id!");
        } else if (!event.isPresent()) {
            return Result.fail("No such event!");
        } else if (event.get().getState() == 1 || event.get().getState() == 2) {
            return Result.fail("Can't take over in-progress/complete event!");
        } else {
            event.get().setTeam_id(team.getTeam_id());
            event.get().setStart_date(new Date(System.currentTimeMillis()));
            event.get().setState(2);
            team.setStatus("busy");

            //saving information
            teamRepo.save(team);
            eventRepo.save(event.get());

            //validate
            Optional<Event> tempEvent = eventRepo.findById(id);
            RescueTeam tempTeam = teamRepo.findByContacts_face_id(face_id);

            if (tempTeam.getStatus().equals("busy") && tempEvent.get().getTeam_id().equals(tempTeam.getTeam_id())) {
                return Result.succ("Take over success!");
            } else {
                return Result.fail("Take over fail!");
            }
        }
    }

    @PostMapping("/complete")
    public Result completeEvent(HttpServletRequest request, @RequestParam(value = "event_id") int id, @RequestParam(value = "report") String report) {
        //validate token
        Result result = JwtUtil.getUserFaceIdByToken(request);
        if (result.getCode().equals("-1")) {
            return Result.fail("Token expired!");
        }
        String face_id = result.getData().toString();
        RescueTeam team = teamRepo.findByContacts_face_id(face_id);
        Optional<Event> event = eventRepo.findById(id);
        if (team == null) {
            return Result.fail("No such team!");
        } else if (id <= 0) {
            return Result.fail("Invalid event id!");
        } else if (!event.isPresent()) {
            return Result.fail("No such event!");
        } else if (event.get().getState() == 0 || event.get().getState() == 1) {
            return Result.fail("Can't complete waiting/complete event!");
        } else {

            event.get().setEnd_date(new Date(System.currentTimeMillis()));
            event.get().setState(1);
            team.setStatus("free");

            //saving information
            teamRepo.save(team);
            eventRepo.save(event.get());

            //validate
            Optional<Event> tempEvent = eventRepo.findById(id);
            RescueTeam tempTeam = teamRepo.findByContacts_face_id(face_id);

            if (tempTeam.getStatus().equals("free") && tempEvent.get().getState() == 1) {
                return Result.succ("Complete event!");
            } else {
                return Result.fail("Complete fail!");
            }
        }
    }

    @PostMapping("/change_status")
    public Result changeStatus(HttpServletRequest request, @RequestParam(value = "status") String status) {
        //validate token
        Result result = JwtUtil.getUserFaceIdByToken(request);
        if (result.getCode().equals("-1")) {
            return Result.fail("Token expired!");
        }
        String face_id = result.getData().toString();
        RescueTeam team = teamRepo.findByContacts_face_id(face_id);
        if (team == null) {
            return Result.fail("No such team!");
        }





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
                List<Map<String, Object>> returnResult = new ArrayList<>();
                for (RescueTeam temp: freeTeam) {
                    Map<String, Object> info = new LinkedHashMap<>();
                    info.put("rescue_team_name", temp.getTeam_name());
                    info.put("contacts_name", temp.getContacts_name());
                    info.put("contacts_phone", temp.getMobile_phone());
                    info.put("personnel_number", temp.getPersonnel_number());
                    info.put("rescue_type", temp.getType());
                    returnResult.add(info);
                }
                return Result.succ("Query success!", returnResult);
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
