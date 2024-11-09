package com.github.accountmanagementproject.alarm.service;

import com.github.accountmanagementproject.alarm.repository.Notification;
import com.github.accountmanagementproject.alarm.repository.NotificationRepository;
import com.github.accountmanagementproject.alarm.repository.NotificationType;
import com.github.accountmanagementproject.alarm.sse.SseEmitters;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitters sseEmitters;


    /**
     * 가입 승인 알림 전송
     * @param recipientUserId 알림을 받을 사용자 ID
     * @param crewId 가입 신청이 들어온 크루의 ID
     * @param masterUserId 크루의 마스터 사용자 ID
     * @param message 알림 메시지 내용
     */
    public void sendApproveNotification(Long recipientUserId, Long crewId, Long masterUserId, String message) {
        createAndSendNotification(recipientUserId, crewId, masterUserId, message, NotificationType.JOIN_REQUEST_APPROVED);
    }

    /**
     * 가입 거절 알림 전송
     * @param recipientUserId 알림을 받을 사용자 ID
     * @param crewId 가입 신청이 들어온 크루의 ID
     * @param masterUserId 크루의 마스터 사용자 ID
     * @param message 알림 메시지 내용
     */
    public void sendRejectNotification(Long recipientUserId, Long crewId, Long masterUserId, String message) {
        createAndSendNotification(recipientUserId, crewId, masterUserId, message, NotificationType.JOIN_REQUEST_REJECTED);
    }

    /**
     * 알림 생성 및 SSE 전송
     * @param recipientUserId 알림을 받을 사용자 ID
     * @param crewId 가입 신청이 들어온 크루의 ID
     * @param masterUserId 크루의 마스터 사용자 ID
     * @param message 알림 메시지 내용
     * @param type 알림 유형 (승인 또는 거절)
     */
    private void createAndSendNotification(Long recipientUserId, Long crewId, Long masterUserId, String message, NotificationType type) {
        // 알림 생성 및 저장
        Notification notification = Notification.builder()
                .recipientUserId(recipientUserId)
                .joinRequestId(crewId)
                .masterUserId(masterUserId)
                .message(message)
                .type(type)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        // SSE를 통해 실시간 알림 전송
        sseEmitters.sendNotification(recipientUserId, message);
    }


    /**
     * 특정 사용자의 알림 목록 조회
     * @param userId 사용자 ID
     * @return 알림 목록
     */
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId);
    }

}
