package net.guides.springboot2.springboot2webappjsp.domain;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "event")
@Data
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    private Integer event_id;

    @Column(name = "user_id", nullable = false, length = 32)
    private Integer user_id;

    @Column(name = "rescue_team_id", nullable = false, length = 32)
    private Integer team_id;

    @Column(name = "event_name", nullable = false)
    private String event_name;

    @Column(name = "event_description", nullable = false, length = 4096)
    private String event_description;

    @Column(name = "event_pic_location")
    private String event_pic_location;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "address", nullable = false, length = 512)
    private String address;

    @Column(name = "submit_date", nullable = false)
    private Date date;

    @Column(name = "rescue_start_date", nullable = false)
    private Date start_date;

    @Column(name = "rescue_report", nullable = false, length = 4096)
    private Date rescue_report;

    @Column(name = "rescue_end_date", nullable = false)
    private Date end_date;

    //rate level from 1 to 10
    @Column(name = "event_level", nullable = false)
    private Integer level;

    //event state: -1 delete, 0 wait, 1 complete, 2 in progress
    @Column(name = "event_state", nullable = false)
    private Integer state;


}
