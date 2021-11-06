package net.guides.springboot2.springboot2webappjsp.repositories;

import net.guides.springboot2.springboot2webappjsp.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {

    @Query(value = "select * from event where user_id = ?1",nativeQuery = true)
    public List<Event> findByUser_id(int id);

    @Query(value = "select * from event where event_state = 0",nativeQuery = true)
    public List<Event> findAllAvailableEvents();

}
