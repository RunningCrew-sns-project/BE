package com.github.runningcrewsnsproject.repository.crew.crewuser;

import com.github.runningcrewsnsproject.web.dto.crew.CrewJoinResponse;

import java.util.List;

public interface CrewsUsersRepositoryCustom {

    List<CrewJoinResponse> findSimpleCrewsUsersByUserEmail(String userEmail);

    List<CrewsUsers> findMyCrewsByEmail(String email, Boolean isAll);

    List<CrewsUsers> findCrewUsersByCrewId(Long crewId, Boolean all);
}
