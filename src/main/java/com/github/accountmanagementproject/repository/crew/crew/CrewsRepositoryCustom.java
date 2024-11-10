package com.github.accountmanagementproject.repository.crew.crew;

import java.util.List;
import java.util.Optional;

public interface CrewsRepositoryCustom {
    Optional<Crew> findCrewDetailByCrewId(Long crewId);

    List<Crew> findIMadeCrewsByEmail(String email);

    boolean isCrewMaster(String masterEmail, Long crewId);



//    List<MyCrewResponse> findMyCrewsByEmail(String email, Boolean isRequesting);
}
