package net.guides.springboot2.springboot2webappjsp.repositories;

import net.guides.springboot2.springboot2webappjsp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface UserRepository extends JpaRepository<User, Integer> {

    //find all face id
    @Query(value = "select user_face_id from user",nativeQuery = true)
    public List<String> findAllID();

    //find user information by face id
    @Query(value = "select * from user where user_face_id = ?1",nativeQuery = true)
    public User findByUser_face_id(String face_id);



}
