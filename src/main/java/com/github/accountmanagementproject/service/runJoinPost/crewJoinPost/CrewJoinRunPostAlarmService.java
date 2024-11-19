package com.github.accountmanagementproject.service.runJoinPost.crewJoinPost;

import com.github.accountmanagementproject.alarm.service.NotificationService;
import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.exception.SimpleRunAppException;
import com.github.accountmanagementproject.exception.enums.ErrorCode;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.repository.crew.crew.CrewsRepository;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsers;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersRepository;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersStatus;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPostRepository;
import com.github.accountmanagementproject.repository.runningPost.crewRunGroup.CrewRunGroup;
import com.github.accountmanagementproject.repository.runningPost.crewRunGroup.CrewRunGroupId;
import com.github.accountmanagementproject.repository.runningPost.crewRunGroup.CrewRunGroupRepository;

import com.github.accountmanagementproject.repository.runningPost.enums.ParticipationStatus;
import com.github.accountmanagementproject.web.dto.crew.CrewJoinResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewParticipantsResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.crewRunGroup.CrewRunJoinResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.crewRunGroup.CrewRunJoinUpdateResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralParticipantsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class CrewJoinRunPostAlarmService {

    private final CrewJoinPostRepository crewJoinPostRepository;
    private final CrewsRepository crewsRepository;
    private final CrewsUsersRepository crewsUsersRepository;
    private final MyUsersRepository userRepository;
    private final CrewRunGroupRepository crewRunGroupRepository;
    private final NotificationService notificationService;
    private final AccountConfig accountConfig;

    private static final int MAX_BAN_COUNT = 3;



    // 신규 참여 신청 시 호출되는 메서드
    @Transactional
    public CrewRunJoinUpdateResponse processNewParticipation(Long crewPostId, Long requestUserId, String principal) {
        MyUser requestUser = accountConfig.findMyUser(principal);
//        MyUser requestUser = userRepository.findByEmail(principal)   //  TODO: 삭제 예정
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND, "User not found with email: " + principal));
        // 강퇴 횟수 확인
        int banCount = crewRunGroupRepository.countByUserUserIdAndStatus(requestUser.getUserId(), ParticipationStatus.FORCED_EXIT);

        if (banCount >= MAX_BAN_COUNT) {
            return autoReject(crewPostId, requestUser.getUserId());
        }

        // 일반 참여 신청 처리
        return approveParticipation(crewPostId, requestUserId, principal);
    }


    // 자동 거절 처리
    private CrewRunJoinUpdateResponse  autoReject(Long crewPostId, Long requestUserId) {
        CrewRunGroup participation = getParticipation(crewPostId, requestUserId);
        CrewJoinPost crewPost = participation.getCrewJoinPost();

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        participation.setStatus(ParticipationStatus.REJECTED);
        participation.setStatusUpdatedAt(now);

        CrewRunGroup savedParticipation = crewRunGroupRepository.save(participation);

        CrewRunJoinUpdateResponse response = CrewRunJoinUpdateResponse.fromEntity(
                CrewRunJoinResponse.toDto(savedParticipation),  // 기존 응답
                null,  // 자동 거절은 관리자가 없음
                ParticipationStatus.REJECTED,  // 새로운 상태
                now  // 업데이트 시간
        );
        // 알림
        notificationService.sendRejectNotification(requestUserId, crewPostId, crewPost.getAuthor().getUserId(), "강퇴 이력이 3번 이상으로 인해 자동으로 거절되었습니다.");
        return response;
    }



    // 참여 신청
    @Transactional
    public CrewRunJoinResponse applyForCrewRun(Long crewPostId, String principal) {
        MyUser requestUser = accountConfig.findMyUser(principal);
//        MyUser requestUser = userRepository.findByEmail(principal)   //  TODO: 삭제 예정
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND, "User not found with email: " + principal));

        // 1. 크루 게시물 조회
        CrewJoinPost crewPost = crewJoinPostRepository.findById(crewPostId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND));

        // 게시글 작성자가 자기가 쓴 글애 참여 X
        if(crewPost.getAuthor().getUserId().equals(requestUser.getUserId())) {
            throw new SimpleRunAppException(ErrorCode.PARTICIPATION_NOT_ALLOWED);
        }

        // 2. 크루 마스터이거나 멤버인지 확인
        validateUserAndCrew(requestUser, crewPost.getCrew().getCrewId());

        // 3. 이미 참여 신청했는지 확인
        validateDuplicateParticipation(requestUser.getUserId(), crewPostId);

        // 4. 최대 인원 확인
        int currentPeople = crewPost.getCurrentPeople() != null ? crewPost.getCurrentPeople() : 0;
        int maxPeople = crewPost.getMaximumPeople() != null ? crewPost.getMaximumPeople() : 0;

        if (currentPeople >= maxPeople) {
            throw new SimpleRunAppException(ErrorCode.GROUP_FULL);
        }

        // 4. 참여 신청 생성
        CrewRunGroupId groupId = new CrewRunGroupId();
        groupId.setUserId(requestUser.getUserId());
        groupId.setCrewPostId(crewPostId);

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        CrewRunGroup participation = CrewRunGroup.builder()
                .id(groupId)
                .user(requestUser)
                .crewJoinPost(crewPost)  // 여기서 crewJoinPost 설정이 필수
                .status(ParticipationStatus.PENDING)
                .joinedAt(now)
                .statusUpdatedAt(now)
                .build();

        CrewRunGroup savedRunGroup = crewRunGroupRepository.save(participation);
        return CrewRunJoinResponse.toDto(savedRunGroup);
    }


    private void validateDuplicateParticipation(Long userId, Long crewPostId) {
        CrewRunGroupId groupId = createGroupId(userId, crewPostId);
        if (crewRunGroupRepository.existsById(groupId)) {
            throw new SimpleRunAppException(ErrorCode.ALREADY_JOINED);
        }
    }

    private CrewRunGroupId createGroupId(Long userId, Long crewPostId) {
        CrewRunGroupId groupId = new CrewRunGroupId();
        groupId.setUserId(userId);
        groupId.setCrewPostId(crewPostId);
        return groupId;
    }

    private Crew validateUserAndCrew(MyUser user, Long crewId) {
        Crew crew = crewsRepository.findById(crewId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.CREW_NOT_FOUND, "Crew not found with ID: " + crewId));

        boolean isCrewMaster = crew.getCrewMaster().getUserId().equals(user.getUserId());
        boolean isCrewMember = crewsUsersRepository.existsByCrewIdAndUserIdAndStatus(
                crewId, user.getUserId(), CrewsUsersStatus.COMPLETED); // 가입 여부와 승인 상태 확인

        if (!isCrewMaster && !isCrewMember) {
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_JOIN);
        }
        return crew;
    }


    // 승인
    public CrewRunJoinUpdateResponse approveParticipation(Long crewPostId, Long requestUserId, String principal) {
        MyUser admin = accountConfig.findMyUser(principal);
//        MyUser admin = userRepository.findByEmail(principal)   //  TODO: 삭제 예정
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND, "User not found with email: " + principal));
        CrewJoinPost crewPost = crewJoinPostRepository.findById(crewPostId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND));

        CrewRunGroup participation = getParticipation(crewPostId, requestUserId);
        validateAdminAuthority(admin, participation.getCrewJoinPost());

        // 2. 신청자의 현재 상태 확인
        if (participation.getStatus() != ParticipationStatus.PENDING) {
            throw new SimpleRunAppException(ErrorCode.INVALID_STATUS);
        }

        // 3. 현재 인원 및 최대 인원 확인
        int currentPeople = crewPost.getCurrentPeople() != null ? crewPost.getCurrentPeople() : 0;
        int maxPeople = crewPost.getMaximumPeople() != null ? crewPost.getMaximumPeople() : 0;

        // 4. 정원 초과 확인
        if (currentPeople >= maxPeople) {
            throw new SimpleRunAppException(ErrorCode.GROUP_FULL);
        }

        // 5. 승인 처리
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        participation.setStatus(ParticipationStatus.APPROVED);
        participation.setApprover(admin);
        participation.setStatusUpdatedAt(now);

        // 6. 현재 인원 증가
        crewPost.setCurrentPeople(currentPeople + 1);
        crewJoinPostRepository.save(crewPost);
        CrewRunJoinUpdateResponse response = CrewRunJoinUpdateResponse.fromEntity( CrewRunJoinResponse.toDto(participation),  // 기존 응답
                admin,                                     // 승인한 관리자
                ParticipationStatus.APPROVED,              // 새로운 상태
                now     );                                   // 업데이트 시간);

        //  알림
        notificationService.sendApproveNotification(requestUserId, crewPostId, admin.getUserId(), "달리기 모임 참여가 승인되었습니다.");
        return response;
    }

    private CrewRunGroup getParticipation(Long crewPostId, Long userId) {
        return crewRunGroupRepository.findByIdWithDetails(crewPostId, userId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.PARTICIPATION_NOT_FOUND));
    }

    private void validateAdminAuthority(MyUser admin, CrewJoinPost post) {
        if (!post.getCrew().getCrewMaster().getUserId().equals(admin.getUserId())) {
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_APPROVAL);
        }
    }


    // 강퇴
    @Transactional
    public CrewRunJoinUpdateResponse forceToKickOut(Long crewPostId, Long requestUserId, String principal) {
        MyUser admin = accountConfig.findMyUser(principal);
//        MyUser admin = userRepository.findByEmail(principal)   //  TODO: 삭제 예정
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND, "User not found with email: " + principal));
        CrewJoinPost crewPost = crewJoinPostRepository.findById(crewPostId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND));

        // 관리자 확인
        CrewRunGroup participation = getParticipation(crewPostId, requestUserId);
        validateAdminAuthority(admin, crewPost);

        // 4. 현재 상태 확인 -  강퇴 가능
        if (participation.getStatus() != ParticipationStatus.APPROVED) {
            throw new SimpleRunAppException(ErrorCode.INVALID_STATUS, "승인된 참여자만 강퇴할 수 있습니다.");
        }

        // 강퇴 처리
        participation.setStatus(ParticipationStatus.FORCED_EXIT);
        LocalDateTime updateTime = LocalDateTime.now();
        participation.setStatusUpdatedAt(updateTime);
        participation.setApprover(admin); // 관리자를 설정
        crewRunGroupRepository.save(participation);

        // 응답 생성
        CrewRunJoinUpdateResponse response =  CrewRunJoinUpdateResponse.fromEntity(CrewRunJoinResponse.toDto(participation), admin, ParticipationStatus.FORCED_EXIT, updateTime);

        notificationService.sendKickNotification(requestUserId, crewPostId, admin.getUserId(), "모임에서 강퇴되었습니다.");  //  알림
        return response;
    }


    // 참여자 리스트 조회
    public List<CrewParticipantsResponse> getAllParticipants(Long postId) {
        return crewRunGroupRepository.findAllParticipantsByPostId(postId).stream()
                .map(CrewParticipantsResponse::toDto)
                .collect(Collectors.toList());
    }







}
