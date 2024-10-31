package com.github.accountmanagementproject.repository.crew.crew;

import com.github.accountmanagementproject.web.dto.crew.CrewDetailResponse;

import java.util.Optional;

public interface CrewsRepositoryCustom {
    Optional<CrewDetailResponse> findCrewDetailByCrewId(Long crewId);


//    List<MyCrewResponse> findMyCrewsByEmail(String email, Boolean isRequesting);
}
