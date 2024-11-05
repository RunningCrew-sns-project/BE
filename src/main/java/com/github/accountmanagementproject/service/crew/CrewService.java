package com.github.accountmanagementproject.service.crew;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.exception.CustomBadCredentialsException;
import com.github.accountmanagementproject.exception.CustomNotFoundException;
import com.github.accountmanagementproject.exception.DuplicateKeyException;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.repository.crew.crew.CrewsRepository;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsers;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersPk;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersRepository;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersStatus;
import com.github.accountmanagementproject.service.mapper.crew.CrewMapper;
import com.github.accountmanagementproject.web.dto.crew.CrewCreationRequest;
import com.github.accountmanagementproject.web.dto.crew.CrewDetailResponse;
import com.github.accountmanagementproject.web.dto.crew.CrewJoinResponse;
import com.github.accountmanagementproject.web.dto.crew.CrewUserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CrewService {
    private final AccountConfig accountConfig;
    private final CrewsRepository crewsRepository;
    private final CrewsUsersRepository crewsUsersRepository;
    private final MyUsersRepository myUsersRepository;

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

    @Transactional(readOnly = true)
    public List<CrewUserResponse> getCrewUsers(String masterEmail, Long crewId, Boolean all) {
        isCrewMaster(masterEmail, crewId);

        List<CrewsUsers> crewsUsers = crewsUsersRepository.findCrewUsersByCrewId(crewId, all);

        return crewsUsers.stream().map(CrewMapper.INSTANCE::crewsUsersToCrewUserResponse).toList();

    }

    private void isCrewMaster(String masterEmail, Long crewId) {
        boolean isCrewMaster = crewsRepository.isCrewMaster(masterEmail, crewId);
        if(!isCrewMaster)
            throw new CustomBadCredentialsException.ExceptionBuilder()
                    .request(masterEmail)
                    .customMessage("크루 마스터가 아닙니다").build();
    }


    //크루원 퇴장시키기
    @Transactional
    public String sendOutCrew(String crewMasterEmail, Long crewId, Integer outCrewId) {
        //내보낼 유저
        MyUser outCrewUser = myUsersRepository.findById(outCrewId).orElseThrow(()->new CustomNotFoundException.ExceptionBuilder()
                .customMessage("해당 유저를 찾을 수 없습니다.").request(outCrewId).build());
        //가입한 크루
        Crew crew = crewsRepository.findById(crewId).orElseThrow(()->new CustomNotFoundException.ExceptionBuilder()
                .customMessage("해당 크루를 찾을 수 없습니다.").request(crewId).build());

        //크루마스터인지 확인
        if(!crewsRepository.isCrewMaster(crewMasterEmail, crewId)){
            throw new CustomBadCredentialsException.ExceptionBuilder()
                    .customMessage("크루의 마스터가 아닙니다.")
                    .build();
        }
        //내보낼 멤버가 크루의 멤버인지 확인
        if(!crew.getCrewUsers().contains(outCrewUser)){
            throw new CustomNotFoundException.ExceptionBuilder()
                    .customMessage("크루의 멤버가 아닙니다.")
                    .build();
        }

        //Crew와 MyUser 객체로 CrewsUsers를 찾기 위한 PK 생성
        CrewsUsersPk crewsUsersPk = new CrewsUsersPk(crew, outCrewUser);

        //PK로 CrewsUsers 검색
        CrewsUsers crewsUser = crewsUsersRepository.findByCrewsUsersPk(crewsUsersPk);

        //CrewsUsers의 상태 변경
        crewsUser.setStatus(CrewsUsersStatus.FORCED_EXIT); //강제 퇴장 상태로 변경
        //강퇴 날짜 지정
        crewsUser.setWithdrawalDate(LocalDateTime.now());

        //객체 저장
        crewsUsersRepository.save(crewsUser);

        return "crewUser : " + crewsUser +" 을/를 성공적으로 퇴장시켰습니다.";
    }

    //가입요청을 확인하여 승인, 거절 로직
    public String approveOrReject(String crewMasterEmail, Long crewId, Integer requestCrewUserId, Boolean approveOrReject) {
        //크루 마스터인지 화인
        if(!crewsRepository.isCrewMaster(crewMasterEmail, crewId)){
            throw new CustomBadCredentialsException.ExceptionBuilder()
                    .customMessage("크루의 마스터가 아닙니다.")
                    .build();
        }

        Crew crew = crewsRepository.findById(crewId).orElseThrow(()->new CustomNotFoundException.ExceptionBuilder()
                .customMessage("해당 크루를 찾을 수 없습니다.").build());

        MyUser requestCrewUser = myUsersRepository.findById(requestCrewUserId).orElseThrow(()->new CustomNotFoundException.ExceptionBuilder()
                .customMessage("해당 유저를 찾을 수 없습니다.").build());

        CrewsUsersPk crewsUsersPk = new CrewsUsersPk(crew, requestCrewUser);

        CrewsUsers crewsUsers = crewsUsersRepository.findByCrewsUsersPk(crewsUsersPk);

        //유저의 상태가 '가입 대기' 상태이고, 승인 요청이라면
        if(crewsUsers.getStatus() == CrewsUsersStatus.WAITING && approveOrReject){
            //'가입 완료' 상태로 바꾸고
            crewsUsers.setStatus(CrewsUsersStatus.COMPLETED);
            //crewsUsers db에 저장
            crewsUsersRepository.save(crewsUsers);
        }
        else {
            //TODO : 가입 거절 시 로직 구현
            crewsUsers.setStatus(CrewsUsersStatus.REJECTED);
            crewsUsersRepository.save(crewsUsers);
        }

        return "요청 유저 : " + requestCrewUser + "의 요청을 " + (approveOrReject ? "승인" : "거절") + "했습니다.";
    }
}
