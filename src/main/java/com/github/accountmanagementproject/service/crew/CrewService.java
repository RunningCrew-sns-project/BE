package com.github.accountmanagementproject.service.crew;

import com.github.accountmanagementproject.alarm.service.NotificationService;
import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.exception.CustomBadCredentialsException;
import com.github.accountmanagementproject.exception.CustomBindException;
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
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPostRepository;
import com.github.accountmanagementproject.service.mapper.crew.CrewMapper;
import com.github.accountmanagementproject.web.dto.crew.*;
import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostResponseMapper;
import com.github.accountmanagementproject.web.dto.infinitescrolling.InfiniteScrollingCollection;
import com.github.accountmanagementproject.web.dto.infinitescrolling.criteria.SearchCriteria;
import com.github.accountmanagementproject.web.dto.infinitescrolling.criteria.SearchRequest;
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
    private final NotificationService notificationService;
    private final CrewJoinPostRepository crewJoinPostRepository;

    @Transactional
    public void crewCreation(@Valid CrewCreationRequest request, String email) {
        MyUser crewMaster = accountConfig.findMyUser(email);
        Crew newCrew = CrewMapper.INSTANCE.crewCreationRequestToCrew(request, crewMaster);
        crewsRepository.save(newCrew);
    }

    private Crew findsCrewById(Long crewId) {
        return crewsRepository.findById(crewId).orElseThrow(() -> new CustomNotFoundException.ExceptionBuilder()
                .customMessage("해당 크루를 찾을 수 없습니다.").request(crewId).build());
    }

    @Transactional
    public CrewJoinResponse joinTheCrew(String email, Long crewId) {
        MyUser user = accountConfig.findMyUser(email);
        Crew crew = findsCrewById(crewId);

        CrewsUsersPk crewsUsersPk = new CrewsUsersPk(crew, user);

        CrewsUsers joinCrewsUsers = validateAndJoinCrew(crewsUsersPk);
        return CrewMapper.INSTANCE.crewsUsersToCrewJoinResponse(joinCrewsUsers);
    }



    private CrewsUsers validateAndJoinCrew(CrewsUsersPk crewsUsersPk) {
        if ( crewsUsersPk.getCrew().getCrewMaster().equals( crewsUsersPk.getUser() ) )
            throw new DuplicateKeyException.ExceptionBuilder()
                    .systemMessage("유효성 검사 실패").customMessage("자기가 만든 크루에 가입할 수 없습니다.").request(crewsUsersPk.getCrew().getCrewName()).build();

        CrewsUsers crewsUsers = crewsUsersRepository.findById(crewsUsersPk)
                .orElseGet(() -> new CrewsUsers(crewsUsersPk));
        checkJoinRequestStatus(crewsUsers);

        return crewsUsersRepository.save(crewsUsers.requestToJoin());
    }

    private void checkJoinRequestStatus(CrewsUsers crewsUsers) {
        boolean isNewRequest = crewsUsers.getStatus() == null;

        if (!isNewRequest && crewsUsers.duplicateRequest()) {
            throw new DuplicateKeyException.ExceptionBuilder()
                    .systemMessage("유효성 검사 실패")
                    .customMessage("이미 가입했거나 가입 요청 중인 크루입니다.")
                    .request(crewsUsers.getStatus())
                    .build();
        } else if (!isNewRequest && LocalDateTime.now().isAfter(crewsUsers.getReleaseDay())) {
            throw new DuplicateKeyException.ExceptionBuilder()
                    .systemMessage("유효성 검사 실패")
                    .customMessage("탈퇴한 또는, 강제 퇴장이나 가입 거절 당하고 재가입 조건을 충족 못한 크루 입니다.")
                    .request("남은 날짜 : " + crewsUsers.getReleaseDay())
                    .build();
        }
    }


    //프론트 테스트를 위한 가입요청내역 반환
    @Transactional(readOnly = true)
    public List<CrewJoinResponse> requestTest(String email) {
        return crewsUsersRepository.findSimpleCrewsUsersByUserEmail(email);
    }

    @Transactional(readOnly = true)
    public CrewDetailResponse getCrewDetail(Long crewId) {
        Crew crew = findsCrewById(crewId);
        long crewMemberCount = crewsUsersRepository.countCrewUsersByCrewId(crewId);
        CrewDetailResponse response = CrewMapper.INSTANCE.crewToCrewDetailResponse(crew);
        response.setMemberCount(crewMemberCount+1);
        return response;
    }

    @Transactional(readOnly = true)
    public List<CrewUserResponse> getCrewUsers(String masterEmail, Long crewId, Boolean all) {
        isCrewMaster(masterEmail, crewId);

        List<CrewsUsers> crewsUsers = crewsUsersRepository.findCrewUsersByCrewId(crewId, all);

        return crewsUsers.stream().map(CrewMapper.INSTANCE::crewsUsersToCrewUserResponse).toList();
    }

    private void isCrewMaster(String masterEmail, Long crewId) {
        boolean isCrewMaster = crewsRepository.isCrewMaster(masterEmail, crewId);
        if (!isCrewMaster)
            throw new CustomBadCredentialsException.ExceptionBuilder()
                    .request(masterEmail)
                    .customMessage("크루 마스터가 아닙니다").build();
    }


    //크루원 퇴장시키기
    @Transactional
    public String sendOutCrew(String crewMasterEmail, Long crewId, Long outUserId) {
//        //내보낼 유저
//        MyUser outCrewUser = myUsersRepository.findById(outCrewId).orElseThrow(()->new CustomNotFoundException.ExceptionBuilder()
//                .customMessage("해당 유저를 찾을 수 없습니다.").request(outCrewId).build());
//        //가입한 크루
//        Crew crew = crewsRepository.findById(crewId).orElseThrow(()->new CustomNotFoundException.ExceptionBuilder()
//                .customMessage("해당 크루를 찾을 수 없습니다.").request(crewId).build());
//
        //크루마스터인지 확인
        if (!crewsRepository.isCrewMaster(crewMasterEmail, crewId)) {
            throw new CustomBadCredentialsException.ExceptionBuilder()
                    .customMessage("크루의 마스터가 아닙니다.")
                    .build();
        }
//        //내보낼 멤버가 크루의 멤버인지 확인
//        if(!crew.getCrewUsers().contains(outCrewUser)){
//            throw new CustomNotFoundException.ExceptionBuilder()
//                    .customMessage("크루의 멤버가 아닙니다.")
//                    .build();
//        }
        //db콜 없이 pk로 객체생성
        Crew myCrew = new Crew();
        myCrew.setCrewId(crewId);
        MyUser outCrewUser = new MyUser();
        outCrewUser.setUserId(outUserId);

        //Crew와 MyUser 객체로 CrewsUsers를 찾기 위한 PK 생성
        CrewsUsersPk crewsUsersPk = new CrewsUsersPk(myCrew, outCrewUser);

        //PK로 CrewsUsers 검색 pk로 검색할수있는 기본 메서드가있어서 그거 사용했습니다!
        CrewsUsers crewsUser = crewsUsersRepository.findById(crewsUsersPk)
                .orElseThrow(() -> new CustomNotFoundException.ExceptionBuilder()
                        .customMessage("해당 크루의 멤버를 찾을 수 없습니다.").request(crewsUsersPk).build());

        //CrewsUsers의 상태 변경
        crewsUser.setStatus(CrewsUsersStatus.FORCED_EXIT); //강제 퇴장 상태로 변경
        //강퇴 날짜 지정
        crewsUser.setWithdrawalDate(LocalDateTime.now());

        //객체 저장 - 트랜잭셔널 적용되어있구 원래있던 객체 불러와서 수정한거라 save 안쓰셔도 괜찮습니다
//        crewsUsersRepository.save(crewsUser);

        return "crewUser : " + crewsUser + " 을/를 성공적으로 퇴장시켰습니다.";
    }

    //가입요청을 확인하여 승인, 거절 로직
    @Transactional
    public String approveOrReject(String email, Long crewId, Long requestCrewUserId, Boolean approveOrReject) {
        //크루 마스터인지 화인
        if (!crewsRepository.isCrewMaster(email, crewId)) {
            throw new CustomBadCredentialsException.ExceptionBuilder()
                    .customMessage("크루의 마스터가 아닙니다.")
                    .build();
        }

        Crew myCrew = new Crew();
        myCrew.setCrewId(crewId);

        MyUser requestCrewUser = new MyUser();
        requestCrewUser.setUserId(requestCrewUserId);

        CrewsUsersPk crewsUsersPk = new CrewsUsersPk(myCrew, requestCrewUser);

        CrewsUsers crewsUser = crewsUsersRepository.findById(crewsUsersPk)
                .orElseThrow(() -> new CustomNotFoundException.ExceptionBuilder()
                        .customMessage("해당 크루의 멤버를 찾을 수 없습니다.").request(crewsUsersPk).build());
        Long masterUserId = myUsersRepository.findByEmail(email).get().getUserId();

        //유저의 상태가 '가입 대기' 상태이고, 승인 요청이라면
        if (crewsUser.getStatus() == CrewsUsersStatus.WAITING && approveOrReject) {
            //'가입 완료' 상태로 바꾸고
            crewsUser.setStatus(CrewsUsersStatus.COMPLETED);
            crewsUser.setJoinDate(LocalDateTime.now());
            // 승인 알림 전송 , requestCrewUserId: 수신자 ID (알림을 받을 사용자) , crewId: 참여 요청된 크루 ID, masterUserId: 크루 마스터 ID
            notificationService.sendApproveNotification(requestCrewUserId, crewId, masterUserId, "크루 가입 요청이 승인되었습니다.");
        }
        else if(crewsUser.getStatus() == CrewsUsersStatus.COMPLETED){
        } else if (crewsUser.getStatus() == CrewsUsersStatus.COMPLETED) {
            return "이미 가입된 유저입니다";
        } else {
            //가입 거절 시 '가입 거절' 로 상태 바꾸고
            crewsUser.setStatus(CrewsUsersStatus.REJECTED);
            crewsUser.setWithdrawalDate(LocalDateTime.now());
            // 거절 알림 전송 , requestCrewUserId: 수신자 ID (알림을 받을 사용자) , crewId: 참여 요청된 크루 ID, masterUserId: 크루 마스터 ID
            notificationService.sendRejectNotification(requestCrewUserId, crewId, masterUserId, "크루 가입 요청이 거절되었습니다.");
        }

        return "요청 유저 : " + requestCrewUser + "의 요청을 " + (approveOrReject ? "승인" : "거절") + "했습니다.";
    }



    // 크루 Info + 크루 달리기 참여 게시글 목록
    @Transactional(readOnly = true)
    public PageResponseDto<CrewDetailWithPostsResponse> getCrewDetailsWithPosts(Long crewId, PageRequestDto pageRequestDto) {

        Crew crew = crewsRepository.findByIdWithImages(crewId)
                .orElseThrow(() -> new CustomNotFoundException.ExceptionBuilder().customMessage("크루를 찾을 수 없습니다.").build());
        CrewListResponse crewResponse = CrewMapper.INSTANCE.crewForListResponse(crew);

        List<CrewJoinPost> crewJoinPosts = crewJoinPostRepository.findFilteredPosts(
                pageRequestDto.getDate(),
                pageRequestDto.getLocation(),
                pageRequestDto.getCursor(),
                pageRequestDto.getSize()
        );

        List<CrewRunPostResponse> postResponses = crewJoinPosts.stream()
                .map(post -> CrewRunPostResponseMapper.toDto(post, crew))
                .toList();

        boolean hasNext = postResponses.size() > pageRequestDto.getSize();
        Integer nextCursor = hasNext ? postResponses.get(postResponses.size() - 1).getRunId().intValue() : null;
        if (hasNext) {
            postResponses = postResponses.subList(0, pageRequestDto.getSize());
        }

        // 5. CrewDetailWithPostsResponse 생성 및 PageResponseDto 반환
        CrewDetailWithPostsResponse response = new CrewDetailWithPostsResponse(crewResponse, postResponses);
        return new PageResponseDto<>(List.of(response), pageRequestDto.getSize(), !hasNext, nextCursor);
    }


}
