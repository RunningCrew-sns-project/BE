package com.github.accountmanagementproject.repository.crew.crew;

import com.github.accountmanagementproject.web.dto.account.crew.UserAboutCrew;
import com.github.accountmanagementproject.web.dto.crew.CrewListResponse;
import com.github.accountmanagementproject.web.dto.crew.MyCrewResponse;
import com.github.accountmanagementproject.web.dto.infinitescrolling.criteria.SearchRequest;

import java.util.List;
import java.util.Optional;

public interface CrewsRepositoryCustom {
    Optional<Crew> findCrewDetailByCrewId(Long crewId);

    List<Crew> findIMadeCrewsByEmail(String email);

    boolean isCrewMaster(String masterEmail, Long crewId);

    List<CrewListResponse> findAvailableCrews(String email, SearchRequest request);

    UserAboutCrew findByIdAndCrewMasterEmail(Long crewId, String email);

    List<MyCrewResponse> findIMadeCrewResponseByEmail(String email);

//    List<MyCrewResponse> findMyCrewsByEmail(String email, Boolean isRequesting);
}
