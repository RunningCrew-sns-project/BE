package com.github.accountmanagementproject.repository.crew.crew;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrewsRepository extends JpaRepository<Crew, Long> {
}
