package com.github.accountmanagementproject.service.runJoinPost.generalJoinPost;

import com.github.accountmanagementproject.alarm.service.NotificationService;
import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.exception.SimpleRunAppException;
import com.github.accountmanagementproject.exception.enums.ErrorCode;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPost;
import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPostRepository;
import com.github.accountmanagementproject.repository.runningPost.runGroup.RunGroup;
import com.github.accountmanagementproject.repository.runningPost.runGroup.RunGroupId;
import com.github.accountmanagementproject.repository.runningPost.runGroup.RunGroupRepository;
import com.github.accountmanagementproject.repository.runningPost.enums.ParticipationStatus;
import com.github.accountmanagementproject.web.dto.runJoinPost.crewRunGroup.CrewRunJoinUpdateResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralParticipantsResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.runGroup.GenRunJoinUpdateResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.runGroup.GeneralJoinResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.runGroup.JoinResponse;
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
public class GeneralJoinRunPostAlarmService {

    private final GeneralJoinPostRepository generalJoinPostRepository;
    private final MyUsersRepository userRepository;
    private final RunGroupRepository runGroupRepository;
    private final NotificationService notificationService;
    private final AccountConfig accountConfig;

    private static final int MAX_BAN_COUNT = 3;


    // 참여 신청
    @Transactional
    public GeneralJoinResponse applyToJoinPost(String email, Long postId) {
        // 1. 사용자 조회
        MyUser user = accountConfig.findMyUser(email);  // TODO: 수정 예정
//        MyUser user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND));

        // 2. 게시글 조회
        GeneralJoinPost post = generalJoinPostRepository.findById(postId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND));

        // 게시글 작성자가 자기가 쓴 글애 참여 X
        if(post.getAuthor().getUserId().equals(user.getUserId())) {
            throw new SimpleRunAppException(ErrorCode.PARTICIPATION_NOT_ALLOWED);
        }

        // 3. 복합키 생성 및 중복 참여 확인
        RunGroupId groupId = new RunGroupId();
        groupId.setUserId(user.getUserId());
        groupId.setGeneralPostId(postId);

        if (runGroupRepository.existsById(groupId)) {
            throw new SimpleRunAppException(ErrorCode.ALREADY_JOINED);
        }

        // 4. 최대 인원 확인
        int currentPeople = post.getCurrentPeople() != null ? post.getCurrentPeople() : 0;
        int maxPeople = post.getMaximumPeople() != null ? post.getMaximumPeople() : 0;

        if (currentPeople >= maxPeople) {
            throw new SimpleRunAppException(ErrorCode.GROUP_FULL);
        }

        // 5. RunGroup 엔티티 생성
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        RunGroup runGroup = RunGroup.builder()
                .id(groupId)
                .user(user)
                .generalJoinPost(post)
                .status(ParticipationStatus.PENDING)
                .joinedAt(now)
                .statusUpdatedAt(now)
                .build();

        RunGroup savedRunGroup = runGroupRepository.save(runGroup);
        return GeneralJoinResponse.toDto(savedRunGroup);
    }


    // 신규 참여 신청 시 호출되는 메서드
    @Transactional
    public GenRunJoinUpdateResponse processNewParticipation(Long postId, Long requestUserId, String email) {
        MyUser user = accountConfig.findMyUser(email);
//        MyUser requestUser = userRepository.findById(Math.toIntExact(requestUserId))  //  TODO: 삭제 예정
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND, "User not found with email: " + email));
        // 강퇴 횟수 확인
        int banCount = countForcedExit(requestUserId);

        if (banCount >= MAX_BAN_COUNT) {
            return autoReject(email, postId, requestUserId);
        }

        return approve(email, postId, requestUserId);
    }

    // 강퇴 이력 확인
    private int countForcedExit(Long userId) {
        int count = runGroupRepository.countByUserUserIdAndStatus(userId, ParticipationStatus.FORCED_EXIT);
        log.info("User ID: {} has {} forced exits", userId, count);
        return count;
    }


    //  거절 : 자동으로 처리 , 강퇴 이력 3번이면 거절
    private GenRunJoinUpdateResponse autoReject(String email, Long postId, Long requestUserId) {
        // 1. 관리자(방장) 조회
//        MyUser user = accountConfig.findMyUser(adminEmail);
        MyUser admin = userRepository.findByEmail(email)   // TODO
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND));

        // 2. 게시물 조회
        GeneralJoinPost post = generalJoinPostRepository.findById(postId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND));

        // 3. 참여 신청 정보 조회
        RunGroupId groupId = new RunGroupId();
        groupId.setUserId(requestUserId);
        groupId.setGeneralPostId(postId);

        RunGroup runGroup = runGroupRepository.findById(groupId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.PARTICIPATION_NOT_FOUND));

        // 4. 자동 거절 처리
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        runGroup.setStatus(ParticipationStatus.REJECTED);
        runGroup.setApprover(admin);
        runGroup.setStatusUpdatedAt(now);
        runGroupRepository.save(runGroup);

        // 5. 알림 전송
        notificationService.sendRejectNotification(requestUserId, postId, admin.getUserId(), "강퇴 이력이 3번 이상으로 인해 자동으로 거절되었습니다.");

        // 6. 응답 생성
        GeneralJoinResponse existingResponse = GeneralJoinResponse.toDto(runGroup);
        return GenRunJoinUpdateResponse.from(existingResponse, admin, ParticipationStatus.REJECTED, now);
    }


    // 승인 또는 반려
    @Transactional
    public GenRunJoinUpdateResponse approve(String email, Long postId, Long userId) {
//        MyUser user = accountConfig.findMyUser(principal);
        MyUser admin = userRepository.findByEmail(email)  // TODO
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND));

        GeneralJoinPost post = generalJoinPostRepository.findById(postId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND));

        if (!post.getAuthor().equals(admin)) {
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_ACCESS, "게시물의 작성자가 아닙니다.");
        }

        // 2. 참여 신청 정보 조회 및 상태 확인
        RunGroupId groupId = new RunGroupId();
        groupId.setUserId(userId);
        groupId.setGeneralPostId(postId);

        RunGroup runGroup = runGroupRepository.findById(groupId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.PARTICIPATION_NOT_FOUND));

        if (runGroup.getStatus() != ParticipationStatus.PENDING) {
            throw new SimpleRunAppException(ErrorCode.INVALID_STATUS, "이미 처리된 신청입니다.");
        }

            // 승인 처리
        int currentPeople = post.getCurrentPeople() != null ? post.getCurrentPeople() : 0;
        int maxPeople = post.getMaximumPeople() != null ? post.getMaximumPeople() : 0;

        if (currentPeople >= maxPeople) {
            throw new SimpleRunAppException(ErrorCode.GROUP_FULL);
        }

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        runGroup.setStatus(ParticipationStatus.APPROVED);
        runGroup.setApprover(admin);
        runGroup.setStatusUpdatedAt(now);
        runGroupRepository.save(runGroup);

        post.setCurrentPeople(currentPeople + 1);
        generalJoinPostRepository.save(post);

        notificationService.sendApproveNotification(userId, postId, admin.getUserId(), "달리기 모임 참여가 승인되었습니다.");   //  알림

        // 6. 응답 생성 (fromEntity 메서드 활용)
        GenRunJoinUpdateResponse response = GenRunJoinUpdateResponse.from(GeneralJoinResponse.toDto(runGroup), admin, runGroup.getStatus(), now);
        return response;
    }


    // 강퇴
    @Transactional
    public GenRunJoinUpdateResponse forceToKickOut(String adminEmail, Long postId, Long userId) {
        // 1. 관리자(방장) 조회
        MyUser admin = accountConfig.findMyUser(adminEmail);
//        MyUser admin = userRepository.findByEmail(adminEmail)
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND));

        // 2. 게시물 조회 및 권한 확인
        GeneralJoinPost post = generalJoinPostRepository.findById(postId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND));

        if (!post.getAuthor().equals(admin)) {
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_ACCESS, "게시물의 작성자가 아닙니다.");
        }

        // 3. 참여 정보 조회
        RunGroupId groupId = new RunGroupId();
        groupId.setUserId(userId);
        groupId.setGeneralPostId(postId);

        RunGroup runGroup = runGroupRepository.findById(groupId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.PARTICIPATION_NOT_FOUND));

        // 4. 현재 상태 확인 -  강퇴 가능
        if (runGroup.getStatus() != ParticipationStatus.APPROVED) {
            throw new SimpleRunAppException(ErrorCode.INVALID_STATUS, "승인된 참여자만 강퇴할 수 있습니다.");
        }

        // 5. 강퇴 처리
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        runGroup.setStatus(ParticipationStatus.FORCED_EXIT);
        runGroup.setApprover(admin);
        runGroup.setStatusUpdatedAt(now);
        runGroupRepository.save(runGroup);

        // 6. 현재 참여 인원 감소
        int currentPeople = post.getCurrentPeople() != null ? post.getCurrentPeople() : 1;
        post.setCurrentPeople(Math.max(0, currentPeople - 1)); // 음수가 되지 않도록 보호
        generalJoinPostRepository.save(post);

        // 7. 강퇴 알림 전송
        notificationService.sendKickNotification(userId, postId, admin.getUserId(), "모임에서 강퇴되었습니다.");  // TODO 알림

        // 8. 응답 생성
        GenRunJoinUpdateResponse response = GenRunJoinUpdateResponse.from(GeneralJoinResponse.toDto(runGroup), admin, ParticipationStatus.FORCED_EXIT, now);
        return response;
    }



    // general_post_id를 조회하면 해당 게시물의 모든 참여자들의 user_id, status , 참여일, 업데이트일 목록 조회
    public List<GeneralParticipantsResponse> getAllParticipants(Long postId) {
        return runGroupRepository.findAllParticipantsByPostId(postId).stream()
                .map(GeneralParticipantsResponse::toDto)
                .collect(Collectors.toList());
    }



}
