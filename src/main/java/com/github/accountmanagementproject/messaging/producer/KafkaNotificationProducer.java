package com.github.accountmanagementproject.messaging.producer;

import com.github.accountmanagementproject.messaging.repository.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class KafkaNotificationProducer {

    private final KafkaTemplate<String, Notification> kafkaTemplate;

    @Value("${spring.kafka.topic.new-entity-topic}")
    private String newEntityTopic;

    public KafkaNotificationProducer(KafkaTemplate<String, Notification> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNotification(Notification notification) {
        kafkaTemplate.send(newEntityTopic, notification);
        System.out.println("Sent notification to Kafka topic: " + newEntityTopic + ", message: " + notification);
    }

}
