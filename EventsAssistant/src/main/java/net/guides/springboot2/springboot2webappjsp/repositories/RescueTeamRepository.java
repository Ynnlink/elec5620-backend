package net.guides.springboot2.springboot2webappjsp.repositories;

import net.guides.springboot2.springboot2webappjsp.domain.RescueTeam;
import net.guides.springboot2.springboot2webappjsp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface RescueTeamRepository extends JpaRepository<RescueTeam, Integer> {

    //find all face id
    @Query(value = "select contacts_face_id from rescue_team",nativeQuery = true)
    public List<String> findAllID();

    //find team information by face id
    @Query(value = "select * from rescue_team where contacts_face_id = ?1",nativeQuery = true)
    public RescueTeam findByContacts_face_id(String face_id);


}