package net.guides.springboot2.springboot2webappjsp.controllers;

import net.guides.springboot2.springboot2webappjsp.authentication.JwtUtil;
import net.guides.springboot2.springboot2webappjsp.domain.Event;
import net.guides.springboot2.springboot2webappjsp.domain.RescueTeam;
import net.guides.springboot2.springboot2webappjsp.domain.User;
import net.guides.springboot2.springboot2webappjsp.repositories.EventRepository;
import net.guides.springboot2.springboot2webappjsp.repositories.RescueTeamRepository;
import net.guides.springboot2.springboot2webappjsp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;


@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    UserRepository userRepo;
    @Autowired
    RescueTeamRepository teamRepo;
    @Autowired
    EventRepository eventRepo;

    @PostMapping
    public Result allocateTeam(HttpServletRequest request,
                               @RequestParam(value = "team_id") int team_id,
                               @RequestParam(value = "event_id") int event_id) {
        //invalid id
        if (team_id <= 0 || event_id <= 0) {
            return Result.fail("Invalid id!");
        }

        //validate token
        Result result = JwtUtil.getUserFaceIdByToken(request);
        if (result.getCode().equals("-1")) {
            return Result.fail("Token expired!");
        }
        String face_id = result.getData().toString();
        User admin = userRepo.findByUser_face_id(face_id);

        if (admin == null) {
            return Result.fail("No such admin exist!");
        } else if (admin.getType().equals("user")) {
            return Result.fail("No authorize!");
        } else {

            Optional<RescueTeam> team = teamRepo.findById(team_id);
            Optional<Event> event = eventRepo.findById(event_id);

            if (!team.isPresent()) {
                return Result.fail("No such team exist!");
            } else if (!event.isPresent()) {
                return Result.fail("No such event exist!");
            } else if (team.get().getStatus().equals("busy")) {
                return Result.fail("Select team is busy!");
            } else {

                event.get().setTeam_id(team_id);
                event.get().setStart_date(new Date(System.currentTimeMillis())); //current time
                event.get().setState(2); //in-progress
                team.get().setStatus("busy");

                //saving information
                teamRepo.save(team.get());
                eventRepo.save(event.get());

                //validate
                Optional<Event> tempEvent = eventRepo.findById(event_id);
                Optional<RescueTeam> tempTeam = teamRepo.findById(team_id);

                if (tempTeam.get().getStatus().equals("busy") && tempEvent.get().getTeam_id().equals(team_id)) {
                    return Result.succ("Allocate success!");
                } else {
                    return Result.fail("Update fail!");
                }
            }
        }
    }
}
