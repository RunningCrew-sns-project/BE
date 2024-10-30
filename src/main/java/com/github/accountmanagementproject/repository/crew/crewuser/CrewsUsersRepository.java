package com.github.accountmanagementproject.repository.crew.crewuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CrewsUsersRepository extends JpaRepository<CrewsUsers, CrewsUsersPk>, CrewsUsersRepositoryCustom {
    @Query("SELECT cu FROM CrewsUsers cu JOIN FETCH cu.crewsUsersPk.crew WHERE cu.crewsUsersPk.user.email = :email")
    List<CrewsUsers> findByMyEmail(String email);
}
