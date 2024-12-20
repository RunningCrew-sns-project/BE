package com.github.accountmanagementproject.repository.crew.crewuser;

import com.github.accountmanagementproject.web.dto.account.crew.UserAboutCrew;
import com.github.accountmanagementproject.web.dto.crew.CrewAndUserResponse;
import com.github.accountmanagementproject.web.dto.crew.CrewJoinResponse;
import com.github.accountmanagementproject.web.dto.crew.MyCrewResponse;

import java.util.List;

public interface CrewsUsersRepositoryCustom {

    List<CrewJoinResponse> findSimpleCrewsUsersByUserEmail(String userEmail);

    List<CrewsUsers> findMyCrewsByEmail(String email, Boolean isAll);

    List<CrewsUsers> findCrewUsersByCrewId(Long crewId, Boolean all);

    long countCrewUsersByCrewId(Long crewId);

    UserAboutCrew findByCrewIdAndUserEmail(Long crewId, String email);

    void findByPkAndPlusCaveatCount(Long crewId, Long badUserId);

    boolean withdrawalCrew(String email, Long crewId);

    List<MyCrewResponse> findMyCrewResponseByEmail(String email, Boolean isAll);

    List<CrewAndUserResponse> myCrewPendingUsers(String email);
}
