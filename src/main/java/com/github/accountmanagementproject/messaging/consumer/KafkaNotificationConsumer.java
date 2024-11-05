package com.github.accountmanagementproject.messaging.consumer;

import com.github.accountmanagementproject.messaging.repository.Notification;
import com.github.accountmanagementproject.messaging.sse.SseEmitters;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class KafkaNotificationConsumer {

    private final SseEmitters sseEmitters;

    public KafkaNotificationConsumer(SseEmitters sseEmitters) {
        this.sseEmitters = sseEmitters;
    }


    @KafkaListener(topics = "${spring.kafka.topic.new-entity-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(Notification notification) {
        System.out.println("Received notification from Kafka topic: " + notification);

        // SSE를 통해 연결된 클라이언트에게 알림 전송
        sseEmitters.sendNotification(notification.getUserId(), notification.getMessage());
    }
}
