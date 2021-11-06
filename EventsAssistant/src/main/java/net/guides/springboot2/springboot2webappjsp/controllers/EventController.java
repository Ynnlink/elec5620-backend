package net.guides.springboot2.springboot2webappjsp.controllers;

import net.guides.springboot2.springboot2webappjsp.authentication.JwtUtil;
import net.guides.springboot2.springboot2webappjsp.domain.Event;
import net.guides.springboot2.springboot2webappjsp.domain.User;
import net.guides.springboot2.springboot2webappjsp.repositories.EventRepository;
import net.guides.springboot2.springboot2webappjsp.repositories.RescueTeamRepository;
import net.guides.springboot2.springboot2webappjsp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("event")
public class EventController {

    @Autowired
    UserRepository userRepo;
    @Autowired
    RescueTeamRepository teamRepo;
    @Autowired
    EventRepository eventRepo;

    //list current user's event
    @GetMapping
    public Result getEvent(HttpServletRequest request) {
        return null;
    }

    //list all event (admin)
    @GetMapping
    public Result getAllEvent(HttpServletRequest request) {
        return null;
    }

    //post a new event
    @PostMapping
    public Result postEvent(HttpServletRequest request, Event event, @RequestParam(value = "file") MultipartFile file) {

        String face_id = JwtUtil.getUserFaceIdByToken(request).getData().toString();
        User user = userRepo.findByUser_face_id(face_id);

        if (user == null) {
            return Result.fail("No such user's information!");
        }
        if (file.getSize() == 0) {
            return Result.fail("File can't be empty!");
        }

        if (StringUtils.isEmpty(event.getEvent_name())) {
            return Result.fail("Event name can't be empty!");
        } else if (StringUtils.isEmpty(event.getEvent_description())) {
            return Result.fail("Event description can't be empty!");
        } else if (StringUtils.isEmpty(event.getCity())) {
            return Result.fail("City can't be empty!");
        } else if (StringUtils.isEmpty(event.getAddress())) {
            return Result.fail("Address can't be empty!");
        } else {
            //saving event
            //upload by who
            event.setUser_id(user.getId());
            //file upload
            String storePath = FileUploader.fileUpload(file);
            if (storePath.equals("fail")) {
                return Result.fail("Upload fail!");
            } else {
                event.setEvent_pic_location(storePath);
            }
            //upload date
            Date date = new Date(System.currentTimeMillis());
            event.setDate(date);
            //default level is 1
            event.setLevel(1);
            //default state is wait
            event.setState(0);

            try {
                eventRepo.save(event);
                return Result.succ("Saving success!");
            } catch (Exception e) {
                e.printStackTrace();
                return Result.fail("Saving fail!");
            }
        }
    }

    //edit a current event
    @PutMapping
    public Result editEvent(HttpServletRequest request, @RequestParam(value = "event_id") String id) {
        return null;
    }

    //delete event
    @DeleteMapping
    public Result deleteEvent(HttpServletRequest request, @RequestParam(value = "event_id") String id) {
        return null;
    }


}
