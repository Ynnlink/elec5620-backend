package net.guides.springboot2.springboot2webappjsp.repositories;

import net.guides.springboot2.springboot2webappjsp.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {
}