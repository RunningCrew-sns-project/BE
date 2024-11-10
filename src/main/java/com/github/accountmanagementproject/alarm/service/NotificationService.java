package com.github.accountmanagementproject.alarm.service;

import com.github.accountmanagementproject.alarm.repository.Notification;
import com.github.accountmanagementproject.alarm.repository.NotificationRepository;
import com.github.accountmanagementproject.alarm.sse.SseEmitters;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitters sseEmitters;


    public void sendJoinRequestNotification(Long masterUserId, Long joinRequestId, String message) {
        Notification notification = createNotification(masterUserId, joinRequestId, message);
        notificationRepository.save(notification);

        // 알림 전송
        sseEmitters.sendNotification(masterUserId, message);
    }

    public void sendApprovalNotification(Long requesterId, Long joinRequestId, String message) {
        Notification notification = createNotification(requesterId, joinRequestId, message);
        notificationRepository.save(notification);

        // 알림 전송
        sseEmitters.sendNotification(requesterId, message);
    }

    private Notification createNotification(Long userId, Long joinRequestId, String message) {
        return Notification.builder()
                .recipientUserId(userId)
                .joinRequestId(joinRequestId)
                .message(message)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();
    }

}
