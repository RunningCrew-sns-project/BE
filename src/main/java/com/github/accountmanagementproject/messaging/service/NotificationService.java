package com.github.accountmanagementproject.messaging.service;

import com.github.accountmanagementproject.messaging.repository.Notification;
import com.github.accountmanagementproject.messaging.producer.KafkaNotificationProducer;
import com.github.accountmanagementproject.messaging.sse.SseEmitters;
import org.springframework.stereotype.Service;


@Service
public class NotificationService {

    private final KafkaNotificationProducer kafkaNotificationProducer;
    private final SseEmitters sseEmitters;

    public NotificationService(KafkaNotificationProducer kafkaNotificationProducer, SseEmitters sseEmitters) {
        this.kafkaNotificationProducer = kafkaNotificationProducer;
        this.sseEmitters = sseEmitters;
    }

    public void notifyUser(Notification notification) {
        // Kafka 토픽에 알림 전송
        kafkaNotificationProducer.sendNotification(notification);
    }

    public void notifyUser(Long userId, String message) {
        Notification notification = new Notification(userId, message);

        // Kafka로 알림 전송
        kafkaNotificationProducer.sendNotification(notification);

        // SSE로 즉시 알림 전송
        sseEmitters.sendNotification(userId, message);
    }
}
