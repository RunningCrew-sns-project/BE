package com.github.accountmanagementproject.repository.crew.crewuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CrewsUsersRepository extends JpaRepository<CrewsUsers, CrewsUsersPk>, CrewsUsersRepositoryCustom {
    @Query("SELECT cu FROM CrewsUsers cu JOIN FETCH cu.crewsUsersPk.crew WHERE cu.crewsUsersPk.user.email = :email")
    List<CrewsUsers> findByMyEmail(String email);

    CrewsUsers findByCrewsUsersPk(CrewsUsersPk crewsUsersPk);
  
    // 특정 사용자가 특정 크루에 가입되어 있고, 승인된 상태인지 확인
    @Query("SELECT COUNT(cu) > 0 FROM CrewsUsers cu " +
            "WHERE cu.crewsUsersPk.crew.crewId = :crewId " +
            "AND cu.crewsUsersPk.user.userId = :userId " +
            "AND cu.status = :status")
    boolean existsByCrewIdAndUserIdAndStatus(@Param("crewId") Long crewId,
                                             @Param("userId") Long userId,
                                             @Param("status") CrewsUsersStatus status);
}
