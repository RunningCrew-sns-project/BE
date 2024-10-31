package com.github.accountmanagementproject.repository.crew.crewuser;

import com.github.accountmanagementproject.web.dto.crews.CrewJoinResponse;

import java.util.List;

public interface CrewsUsersRepositoryCustom {

    List<CrewJoinResponse> findSimpleCrewsUsersByUserEmail(String userEmail);
}
