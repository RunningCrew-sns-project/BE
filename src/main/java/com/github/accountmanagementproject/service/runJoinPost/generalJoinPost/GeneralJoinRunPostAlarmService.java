package com.github.accountmanagementproject.service.runJoinPost.generalJoinPost;

import com.github.accountmanagementproject.alarm.service.NotificationService;
import com.github.accountmanagementproject.exception.SimpleRunAppException;
import com.github.accountmanagementproject.exception.enums.ErrorCode;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPost;
import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPostRepository;
import com.github.accountmanagementproject.repository.runningPost.runGroup.RunGroup;
import com.github.accountmanagementproject.repository.runningPost.runGroup.RunGroupId;
import com.github.accountmanagementproject.repository.runningPost.runGroup.RunGroupRepository;
import com.github.accountmanagementproject.repository.runningPost.userRunGroups.ParticipationStatus;
import com.github.accountmanagementproject.web.dto.runJoinPost.runGroup.JoinResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;


@Slf4j
@RequiredArgsConstructor
@Service
public class GeneralJoinRunPostAlarmService {

    private final GeneralJoinPostRepository generalJoinPostRepository;
    private final MyUsersRepository userRepository;
    private final RunGroupRepository runGroupRepository;
    private final NotificationService notificationService;


    // 참여 신청
    @Transactional
    public JoinResponse applyToJoinPost(String email, Long postId) {
        // 1. 사용자 조회
        MyUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND));

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
        return JoinResponse.toDto(savedRunGroup);
    }


    // 승인 또는 반려
    @Transactional
    public String approveOrReject(String adminEmail, Long postId, Long userId, Boolean approve) {
        // 1. 관리자(방장) 조회
        MyUser admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND));

        // 2. 게시물 조회 및 권한 확인
        GeneralJoinPost post = generalJoinPostRepository.findById(postId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND));

        if (!post.getAuthor().equals(admin)) {
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_ACCESS, "게시물의 작성자가 아닙니다.");
        }

        // 3. 참여 신청 정보 조회
        RunGroupId groupId = new RunGroupId();
        groupId.setUserId(userId);
        groupId.setGeneralPostId(postId);

        RunGroup runGroup = runGroupRepository.findById(groupId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.PARTICIPATION_NOT_FOUND));

        // 4. 현재 상태 확인
        if (runGroup.getStatus() != ParticipationStatus.PENDING) {
            return "이미 처리된 신청입니다.";
        }

        // 5. 승인/거절 처리
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        if (approve) {
            // 승인 처리
            int currentPeople = post.getCurrentPeople() != null ? post.getCurrentPeople() : 0;
            int maxPeople = post.getMaximumPeople() != null ? post.getMaximumPeople() : 0;

            // 승인 처리
            if (currentPeople >= maxPeople) {
                throw new SimpleRunAppException(ErrorCode.GROUP_FULL);
            }
            runGroup.setStatus(ParticipationStatus.APPROVED);
            post.setCurrentPeople(currentPeople + 1);  // null 체크된 값을 사용
            generalJoinPostRepository.save(post);

            notificationService.sendApproveNotification(userId, postId, admin.getUserId(), "달리기 모임 참여가 승인되었습니다.");
        } else {
            // 거절 처리
            runGroup.setStatus(ParticipationStatus.REJECTED);
            notificationService.sendRejectNotification(userId, postId, admin.getUserId(), "달리기 모임 참여가 거절되었습니다.");
        }

        // 6. 공통 처리
        runGroup.setApprover(admin);
        runGroup.setStatusUpdatedAt(now);
        runGroupRepository.save(runGroup);

        String actionType = approve ? "승인" : "거절";
        log.info("Admin {} {} participation for user {} in post {}", admin.getEmail(), actionType, userId, postId);

        return String.format("요청 유저: %s의 요청을 %s했습니다.", runGroup.getUser().getNickname(), actionType);
    }


    // 강퇴
    @Transactional
    public String forceToKickOut(String adminEmail, Long postId, Long userId) {
        // 1. 관리자(방장) 조회
        MyUser admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND));

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

        // 4. 현재 상태 확인 - 승인된 상태인 경우에만 강퇴 가능
        if (runGroup.getStatus() != ParticipationStatus.APPROVED) {
            return "승인된 참여자만 강퇴할 수 있습니다.";
        }

        // 5. 강퇴 처리
        runGroup.setStatus(ParticipationStatus.FORCED_EXIT);
        runGroup.setApprover(admin);
        runGroup.setStatusUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")));

        // 6. 현재 참여 인원 감소
        int currentPeople = post.getCurrentPeople() != null ? post.getCurrentPeople() : 1;
        post.setCurrentPeople(currentPeople - 1);
        generalJoinPostRepository.save(post);

        // 7. 강퇴 알림 전송
        notificationService.sendKickNotification(userId, postId, admin.getUserId(), "모임에서 강퇴되었습니다.");

        runGroupRepository.save(runGroup);

        log.info("Admin {} kicked user {} from post {}", admin.getEmail(), userId, postId);

        return String.format("참여자(%s)를 강퇴했습니다.", runGroup.getUser().getNickname());
    }



}
