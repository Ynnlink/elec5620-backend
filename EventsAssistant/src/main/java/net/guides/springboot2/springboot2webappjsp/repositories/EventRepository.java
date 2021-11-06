package net.guides.springboot2.springboot2webappjsp.repositories;

import net.guides.springboot2.springboot2webappjsp.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {

    //find events uploaded by a certain user
    @Query(value = "select * from event where user_id = ?1",nativeQuery = true)
    public List<Event> findByUser_id(int id);

    //find all waiting events
    @Query(value = "select * from event where event_state = 0",nativeQuery = true)
    public List<Event> findAllAvailableEvents();

    //find in-complete event allocated to a certain team
    @Query(value = "select * from event where rescue_team_id = ?1 and event_state != 2",nativeQuery = true)
    public Event findByTeam_id(int team_id);

}
