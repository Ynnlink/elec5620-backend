package net.guides.springboot2.springboot2webappjsp.repositories;

import net.guides.springboot2.springboot2webappjsp.domain.RescueTeam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RescueTeamRepository extends JpaRepository<RescueTeam, Integer> {
}