package com.github.accountmanagementproject.repository.crew.crewuser;

import com.github.accountmanagementproject.web.dto.crew.CrewJoinResponse;

import java.util.List;

public interface CrewsUsersRepositoryCustom {

    List<CrewJoinResponse> findSimpleCrewsUsersByUserEmail(String userEmail);

    List<CrewsUsers> findMyCrewsByEmail(String email, Boolean isAll);
}
