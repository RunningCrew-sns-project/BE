package com.github.accountmanagementproject.service.crews;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.account.users.MyUsersJpa;
import com.github.accountmanagementproject.repository.crew.crews.Crew;
import com.github.accountmanagementproject.repository.crew.crews.CrewsJpa;
import com.github.accountmanagementproject.repository.crew.crewsUsers.CrewsUsers;
import com.github.accountmanagementproject.repository.crew.crewsUsers.CrewsUsersJpa;
import com.github.accountmanagementproject.repository.crew.crewsUsers.CrewsUsersPk;
import com.github.accountmanagementproject.service.customExceptions.CustomBadCredentialsException;
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
    private final MyUsersJpa myUsersJpa;

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


    //크루원 퇴장시키기
    @Transactional
    public String sendOutCrew(String crewMasterEmail, Long crewId, Integer outCrewId) {
        //요청한 사람 유저 확인
        MyUser crewMaster = accountConfig.findMyUser(crewMasterEmail);

        //내보낼 유저
        MyUser outCrewUser = myUsersJpa.findById(outCrewId).orElseThrow(()->new CustomNotFoundException.ExceptionBuilder()
                .customMessage("해당 유저를 찾을 수 없습니다.").request(outCrewId).build());

        Crew crew = crewsJpa.findById(crewId).orElseThrow(()->new CustomNotFoundException.ExceptionBuilder()
                .customMessage("해당 크루를 찾을 수 없습니다.").request(crewId).build());

        //크루마스터인지 확인
        if(!crew.getCrewMaster().equals(crewMaster)){
            throw new CustomBadCredentialsException.ExceptionBuilder()
                    .customMessage("크루 마스터가 아닙니다")
                    .build();
        }

        CrewsUsersPk crewsUsersPk = new CrewsUsersPk(crew, outCrewUser);

        CrewsUsers crewsUser = crewsUsersJpa.findByCrewsUsersPk(crewsUsersPk);

        crewsUsersJpa.delete(crewsUser);

        return "crewUser : " + crewsUser +" 을/를 성공적으로 퇴장시켰습니다.";
    }
}
