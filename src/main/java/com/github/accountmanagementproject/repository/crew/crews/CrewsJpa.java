package com.github.accountmanagementproject.repository.crew.crews;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrewsJpa extends JpaRepository<Crew, Long> {
}
