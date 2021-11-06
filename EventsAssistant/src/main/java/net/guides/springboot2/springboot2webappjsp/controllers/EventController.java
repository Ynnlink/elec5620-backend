package net.guides.springboot2.springboot2webappjsp.controllers;

import net.guides.springboot2.springboot2webappjsp.authentication.JwtUtil;
import net.guides.springboot2.springboot2webappjsp.domain.Event;
import net.guides.springboot2.springboot2webappjsp.domain.RescueTeam;
import net.guides.springboot2.springboot2webappjsp.domain.User;
import net.guides.springboot2.springboot2webappjsp.repositories.EventRepository;
import net.guides.springboot2.springboot2webappjsp.repositories.RescueTeamRepository;
import net.guides.springboot2.springboot2webappjsp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("event")
public class EventController {

    @Autowired
    UserRepository userRepo;
    @Autowired
    RescueTeamRepository teamRepo;
    @Autowired
    EventRepository eventRepo;

    //list current user's corresponding event
    @GetMapping
    public Result getEvent(HttpServletRequest request) {

        //validate token
        Result result = JwtUtil.getUserFaceIdByToken(request);
        if (result.getCode().equals("-1")) {
            return Result.fail("Token expired!");
        }
        String face_id = result.getData().toString();

        //find user
        User user = userRepo.findByUser_face_id(face_id);
        if (user == null) {
            return Result.fail("No such user's information!");
        }

        //find all relevant events
        List<Event> events = eventRepo.findByUser_id(user.getId());
        if (events.size() == 0) {
            return Result.fail("No events!");
        }

        //build a return list
        List<Map<String, Object>> eventResult = new ArrayList<>();

        for (Event temp: events) {
            Map<String, Object> info = new LinkedHashMap<>();

            //basic information
            info.put("event_name", temp.getEvent_name());
            info.put("event_description", temp.getEvent_description());
            info.put("address", temp.getAddress());
            info.put("submit_date", temp.getDate());
            info.put("start_date", temp.getStart_date());
            info.put("end_date", temp.getEnd_date());
            info.put("pic_location", temp.getEvent_pic_location());

            //additional information
            if (temp.getState() == 0) {
                info.put("state", "Waiting");
            } else if (temp.getState() == 2) {
                Optional<RescueTeam> team = teamRepo.findById(temp.getTeam_id());
                info.put("state", "In-progress");
                info.put("Rescue team", team.get().getTeam_name());
                info.put("Contact information", team.get().getMobile_phone());
            } else if (temp.getState() == 1) {
                Optional<RescueTeam> team = teamRepo.findById(temp.getTeam_id());
                info.put("state", "Complete");
                info.put("Rescue team", team.get().getTeam_name());
                info.put("Report", temp.getRescue_report());
            }

            eventResult.add(info);
        }

        return Result.succ("Query success!", eventResult);
    }

    //list all events (admin)
    //list all waiting events (team)
    @GetMapping("/all")
    public Result getAllEvent(HttpServletRequest request) {
        //validate token
        Result result = JwtUtil.getUserFaceIdByToken(request);
        if (result.getCode().equals("-1")) {
            return Result.fail("Token expired!");
        }
        String face_id = result.getData().toString();

        //find admin and validate
        User user = userRepo.findByUser_face_id(face_id);
        //find team
        RescueTeam team = teamRepo.findByContacts_face_id(face_id);

        if (user == null) {
            if (team == null) {
                return Result.fail("No such user's information!");
            } else {
                //return waiting events for team
                List<Event> events = eventRepo.findAllAvailableEvents();
                if (events.size() == 0) {
                    return Result.fail("No events!");
                }

                //build a return list
                List<Map<String, Object>> eventResult = new ArrayList<>();

                for (Event temp: events) {
                    Map<String, Object> info = new LinkedHashMap<>();

                    Optional<User> correspondingUser = userRepo.findById(temp.getUser_id());

                    //basic information
                    info.put("event_name", temp.getEvent_name());
                    info.put("event_description", temp.getEvent_description());
                    info.put("address", temp.getAddress());
                    info.put("submit_date", temp.getDate());
                    info.put("pic_location", temp.getEvent_pic_location());
                    info.put("level", temp.getLevel());

                    //additional information
                    info.put("Contacts name", correspondingUser.get().getUser_name());
                    info.put("Contacts phone", correspondingUser.get().getTelephone());

                    eventResult.add(info);
                }
                return Result.succ("Query success!", eventResult);
            }

        } else if (user.getType().equals("user")) {
            return Result.fail("No authorize!");
        } else {
            //return all events for admin
            List<Event> events = eventRepo.findAll();
            if (events.size() == 0) {
                return Result.fail("No events!");
            }
            return Result.succ("Query success! (State: 0 wait, 1 complete, 2 in progress)", events);
        }

    }

    //post a new event
    @PostMapping
    public Result postEvent(HttpServletRequest request, Event event, @RequestParam(value = "file") MultipartFile file) {

        //validate token
        Result result = JwtUtil.getUserFaceIdByToken(request);
        if (result.getCode().equals("-1")) {
            return Result.fail("Token expired!");
        }
        String face_id = result.getData().toString();

        //find user
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
    public Result editEvent(HttpServletRequest request,
                            @RequestParam(value = "event_id") int id,
                            @RequestParam(value = "address") String address,
                            @RequestParam(value = "description") String description) {
        //validate token
        Result result = JwtUtil.getUserFaceIdByToken(request);
        if (result.getCode().equals("-1")) {
            return Result.fail("Token expired!");
        }
        String face_id = result.getData().toString();

        //find user
        User user = userRepo.findByUser_face_id(face_id);
        if (user == null) {
            return Result.fail("No such user's information!");
        }

        //find corresponding event
        Optional<Event> event = eventRepo.findById(id);

        if (!event.isPresent()) {
            return Result.fail("No such event exist!");
        } else if (StringUtils.isEmpty(address)) {
            return Result.fail("Address can't be empty!");
        } else if (StringUtils.isEmpty(description)) {
            return Result.fail("Description can't be empty");
        } else {
            //user can only change its events
            if (user.getType().equals("user")) {
                if (event.get().getUser_id() == user.getId()) {
                    event.get().setAddress(address);
                    event.get().setEvent_description(description);
                    eventRepo.save(event.get());
                } else {
                    return Result.fail("Can't change others' events!");
                }
            } else {
                //admin can change all event
                event.get().setAddress(address);
                event.get().setEvent_description(description);
                eventRepo.save(event.get());
            }
            //validate
            Optional<Event> temp = eventRepo.findById(id);
            if (temp.get().getAddress().equals(address) && temp.get().getEvent_description().equals(description)) {
                return Result.succ("Update success!");
            } else {
                return Result.fail("Update fail!");
            }
        }

    }


    //rate a event (admin)
    @PostMapping("/rate")
    public Result editEvent(HttpServletRequest request, @RequestParam(value = "level") int level, @RequestParam(value = "event_id") int id) {
        //validate token
        Result result = JwtUtil.getUserFaceIdByToken(request);
        if (result.getCode().equals("-1")) {
            return Result.fail("Token expired!");
        }
        String face_id = result.getData().toString();

        //find admin
        User admin = userRepo.findByUser_face_id(face_id);
        if (admin == null) {
            return Result.fail("No such admin information!");
        } else if (admin.getType().equals("user")) {
            return Result.fail("No authorize!");
        } else {
            Optional<Event> event = eventRepo.findById(id);

            if (!event.isPresent()) {
                return Result.fail("No such event!");
            } else {
                if (event.get().getState() == 1 || event.get().getState() == 2) {
                    return Result.fail("Can't rate a complete/in-progress event!");
                }
                event.get().setLevel(level);
                eventRepo.save(event.get());
                //validate
                Optional<Event> temp = eventRepo.findById(id);
                if (temp.get().getLevel() == level) {
                    return Result.succ("Rate success!");
                } else {
                    return Result.fail("Rate fail!");
                }
            }
        }
    }


    //delete event
    @DeleteMapping
    public Result deleteEvent(HttpServletRequest request, @RequestParam(value = "event_id") int id) {
        //validate token
        Result result = JwtUtil.getUserFaceIdByToken(request);
        if (result.getCode().equals("-1")) {
            return Result.fail("Token expired!");
        }
        String face_id = result.getData().toString();

        //find user
        User user = userRepo.findByUser_face_id(face_id);
        if (user == null) {
            return Result.fail("No such user's information!");
        } else if (user.getType().equals("user")) {
            //user function
            Optional<Event> event = eventRepo.findById(id);

            if (!event.isPresent()) {
                return Result.fail("No such event exist!");
            } else {

                if (event.get().getState() == 2) {
                    return Result.fail("Can't delete a in-progress event!");
                } else {
                    eventRepo.deleteById(id);
                    Optional<Event> temp = eventRepo.findById(id);
                    if (!temp.isPresent()) {
                        return Result.succ("Delete success!");
                    } else {
                        return Result.fail("Delete fail!");
                    }
                }
            }
        } else {
            //admin function
            eventRepo.deleteById(id);
            Optional<Event> temp = eventRepo.findById(id);
            if (!temp.isPresent()) {
                return Result.succ("Delete success!");
            } else {
                return Result.fail("Delete fail!");
            }
        }
    }


}
