package com.github.accountmanagementproject.messaging.repository;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @Column(name = "recipient_user_id", nullable = false)
    private Long recipientUserId;

    @Column(name = "join_request_id", nullable = false)
    private Long joinRequestId;  // 참여 그룹의 ID

    @Column(name = "master_user_id", nullable = false)
    private Long masterUserId;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;  // 알림 생성 시점

    @Column(name = "sent_at")
    private LocalDateTime sentAt;  // 실제 사용자에게 전송된 시점

    @Enumerated(EnumType.STRING)
    private NotificationType type; // 알림 유형

    public Notification(Long userId, String message) {
        this.recipientUserId = userId;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    public Long getUserId() {
        return recipientUserId;
    }

}

