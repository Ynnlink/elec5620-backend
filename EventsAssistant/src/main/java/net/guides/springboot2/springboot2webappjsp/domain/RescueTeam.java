package net.guides.springboot2.springboot2webappjsp.domain;


import lombok.Data;

import javax.persistence.*;

@Table(name = "rescue_team")
@Data
@Entity
public class RescueTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer team_id;

    @Column(name = "contacts_face_id", nullable = false)
    private String contacts_face_id;

    @Column(name = "rescue_team_name", nullable = false)
    private String team_name;

    @Column(name = "contacts_name", nullable = false)
    private String contacts_name;

    @Column(name = "mobile_phone", nullable = false)
    private String mobile_phone;

    @Column(name = "address", nullable = false, length = 1024)
    private String address;

    @Column(name = "personnel_number", nullable = false, length = 32)
    private Integer personnel_number;

    @Column(name = "rescue_type", nullable = false, length = 1024)
    private String type;

    //free and busy
    @Column(name = "status", nullable = false)
    private String status;


}
