package com.github.accountmanagementproject.repository.crew.crew;

import com.github.accountmanagementproject.web.dto.pagination.SearchCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CrewsRepositoryCustom {
    Optional<Crew> findCrewDetailByCrewId(Long crewId);

    List<Crew> findIMadeCrewsByEmail(String email);

    boolean isCrewMaster(String masterEmail, Long crewId);

    List<Crew> findAvailableCrews(String email, Pageable pageable, SearchCriteria criteria);


//    List<MyCrewResponse> findMyCrewsByEmail(String email, Boolean isRequesting);
}
