package com.github.accountmanagementproject.service.crew;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.exception.CustomNotFoundException;
import com.github.accountmanagementproject.exception.DuplicateKeyException;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.repository.crew.crew.CrewsRepository;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsers;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersPk;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersRepository;
import com.github.accountmanagementproject.service.mapper.crew.CrewMapper;
import com.github.accountmanagementproject.web.dto.crew.CrewCreationRequest;
import com.github.accountmanagementproject.web.dto.crew.CrewDetailResponse;
import com.github.accountmanagementproject.web.dto.crew.CrewJoinResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CrewService {
    private final AccountConfig accountConfig;
    private final CrewsRepository crewsRepository;
    private final CrewsUsersRepository crewsUsersRepository;

    @Transactional
    public void crewCreation(@Valid CrewCreationRequest request, String email) {
        MyUser crewMaster = accountConfig.findMyUser(email);
        Crew newCrew = CrewMapper.INSTANCE.crewCreationRequestToCrew(request, crewMaster);
        crewsRepository.save(newCrew);
    }

    @Transactional
    public CrewJoinResponse joinTheCrew(String email, Long crewId) {
        MyUser user = accountConfig.findMyUser(email);
        Crew crew = crewsRepository.findById(crewId).orElseThrow(()->new CustomNotFoundException.ExceptionBuilder()
                .customMessage("해당 크루를 찾을 수 없습니다.").request(crewId).build());
        CrewsUsersPk crewsUsersPk = new CrewsUsersPk(crew, user);

        CrewsUsers joinCrewsUsers = crewJoinValidationCheckAndSave(crew, user, crewsUsersPk);
        return CrewMapper.INSTANCE.crewsUsersToCrewJoinResponse(joinCrewsUsers);
    }

    private CrewsUsers crewJoinValidationCheckAndSave(Crew crew, MyUser user, CrewsUsersPk crewsUsersPk) {
        if(crew.getCrewMaster().equals(user))
            throw new DuplicateKeyException.ExceptionBuilder()
                    .systemMessage("유효성 검사 실패").customMessage("자기가 만든 크루에 가입할 수 없습니다.").request(crew.getCrewName()).build();
        if(crewsUsersRepository.existsById(crewsUsersPk))
            throw new DuplicateKeyException.ExceptionBuilder()
                    .systemMessage("유효성 검사 실패").customMessage("이미 가입했거나 가입 요청 중인 크루입니다.").request(crew.getCrewName()).build();
        CrewsUsers joinCrewsUsers = new CrewsUsers(crewsUsersPk);
        return crewsUsersRepository.save(joinCrewsUsers.requestToJoin());
    }

    //프론트 테스트를 위한 가입요청내역 반환
    @Transactional(readOnly = true)
    public List<CrewJoinResponse> requestTest(String email) {
        return crewsUsersRepository.findSimpleCrewsUsersByUserEmail(email);
    }

    @Transactional(readOnly = true)
    public CrewDetailResponse getCrewDetail(Long crewId) {
        return crewsRepository.findCrewDetailByCrewId(crewId).orElseThrow(()->new CustomNotFoundException.ExceptionBuilder()
                .customMessage("해당 크루를 찾을 수 없습니다.").request(crewId).build());

    }
}
