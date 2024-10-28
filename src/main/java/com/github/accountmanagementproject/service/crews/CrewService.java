package com.github.accountmanagementproject.service.crews;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.crew.crews.Crew;
import com.github.accountmanagementproject.repository.crew.crews.CrewsJpa;
import com.github.accountmanagementproject.service.mappers.crews.CrewMapper;
import com.github.accountmanagementproject.web.dto.crews.CrewCreationRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrewService {
    private final AccountConfig accountConfig;
    private final CrewsJpa crewsJpa;

    @Transactional
    public void crewCreation(@Valid CrewCreationRequest request, String email) {
        MyUser crewMaster = accountConfig.findMyUser(email);
        Crew newCrew = CrewMapper.INSTANCE.crewCreationRequestToCrew(request, crewMaster);
        crewsJpa.save(newCrew);
    }
}
