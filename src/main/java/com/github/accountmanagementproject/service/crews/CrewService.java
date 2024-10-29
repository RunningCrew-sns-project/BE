package com.github.accountmanagementproject.service.crews;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.crew.crews.Crew;
import com.github.accountmanagementproject.repository.crew.crews.CrewsJpa;
import com.github.accountmanagementproject.repository.crew.crewsUsers.CrewsUsers;
import com.github.accountmanagementproject.repository.crew.crewsUsers.CrewsUsersJpa;
import com.github.accountmanagementproject.repository.crew.crewsUsers.CrewsUsersPk;
import com.github.accountmanagementproject.service.customExceptions.CustomNotFoundException;
import com.github.accountmanagementproject.service.customExceptions.DuplicateKeyException;
import com.github.accountmanagementproject.service.mappers.crews.CrewMapper;
import com.github.accountmanagementproject.web.dto.crews.CrewCreationRequest;
import com.github.accountmanagementproject.web.dto.crews.CrewJoinResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CrewService {
    private final AccountConfig accountConfig;
    private final CrewsJpa crewsJpa;
    private final CrewsUsersJpa crewsUsersJpa;

    @Transactional
    public void crewCreation(@Valid CrewCreationRequest request, String email) {
        MyUser crewMaster = accountConfig.findMyUser(email);
        Crew newCrew = CrewMapper.INSTANCE.crewCreationRequestToCrew(request, crewMaster);
        crewsJpa.save(newCrew);
    }

    @Transactional
    public CrewJoinResponse joinTheCrew(String email, Long crewId) {
        MyUser user = accountConfig.findMyUser(email);
        Crew crew = crewsJpa.findById(crewId).orElseThrow(()->new CustomNotFoundException.ExceptionBuilder()
                .customMessage("해당 크루를 찾을 수 없습니다.").request(crewId).build());
        CrewsUsersPk crewsUsersPk = new CrewsUsersPk(crew, user);

        CrewsUsers joinCrewsUsers = crewJoinValidationCheckAndSave(crew, user, crewsUsersPk);
        return CrewMapper.INSTANCE.joinCrewToCrewJoinResponse(joinCrewsUsers);
    }

    private CrewsUsers crewJoinValidationCheckAndSave(Crew crew, MyUser user, CrewsUsersPk crewsUsersPk) {
        if(crew.getCrewMaster().equals(user))
            throw new DuplicateKeyException.ExceptionBuilder()
                    .systemMessage("유효성 검사 실패").customMessage("자기가 만든 크루에 가입할 수 없습니다.").request(crew.getCrewName()).build();
        if(crewsUsersJpa.existsById(crewsUsersPk))
            throw new DuplicateKeyException.ExceptionBuilder()
                    .systemMessage("유효성 검사 실패").customMessage("이미 가입했거나 가입 요청 중인 크루입니다.").request(crew.getCrewName()).build();
        return crewsUsersJpa.save(new CrewsUsers(crewsUsersPk));
    }

    //테스트를 위한 가입요청내역 반환
    public List<CrewJoinResponse> requestTest(String email) {
        List<CrewsUsers> crewsUsers = crewsUsersJpa.findByMyEmail(email);
        return crewsUsers.stream().map(CrewMapper.INSTANCE::joinCrewToCrewJoinResponse).toList();
    }
}
