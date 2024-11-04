package com.github.accountmanagementproject.repository.crew.crewsUsers;

import com.github.accountmanagementproject.repository.account.users.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CrewsUsersJpa extends JpaRepository<CrewsUsers, CrewsUsersPk> {
    @Query("SELECT cu FROM CrewsUsers cu JOIN FETCH cu.crewsUsersPk.crew WHERE cu.crewsUsersPk.user.email = :email")
    List<CrewsUsers> findByMyEmail(String email);

    CrewsUsers findByCrewsUsersPk(CrewsUsersPk crewsUsersPk);
}
