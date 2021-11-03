package net.guides.springboot2.springboot2webappjsp.repositories;

import net.guides.springboot2.springboot2webappjsp.domain.RescueTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RescueTeamRepository extends JpaRepository<RescueTeam, Integer> {

    //find all face id
    @Query(value = "select contacts_face_id from rescue_team",nativeQuery = true)
    public List<String> findAllID();


}