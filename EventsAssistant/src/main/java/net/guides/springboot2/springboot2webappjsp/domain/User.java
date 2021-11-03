package net.guides.springboot2.springboot2webappjsp.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;

@Table(name = "user")
@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer id;

    @Column(name = "user_face_id", nullable = false, length = 128)
    private String user_face_id;

    @Column(name = "user_name", nullable = false, length = 128)
    private String user_name;

    @Column(name = "telephone", nullable = false, length = 128)
    private String telephone;

    //1 for refugees, 2 for admins
    @Column(name = "user_type", nullable = false)
    private Integer type;


}