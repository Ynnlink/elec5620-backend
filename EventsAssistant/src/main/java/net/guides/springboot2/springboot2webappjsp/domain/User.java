package net.guides.springboot2.springboot2webappjsp.domain;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;

@Table(name = "user")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer id;

    @Column(name = "user_face_id", nullable = false, length = 255)
    private String user_face_id;


    public User() {

    }

    public String getUser_face_id() {
        return user_face_id;
    }

    public void setUser_face_id(String user_face_id) {
        this.user_face_id = user_face_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}