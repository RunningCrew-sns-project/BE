package com.github.accountmanagementproject.messaging.controller;

import com.github.accountmanagementproject.messaging.service.NotificationService;
import com.github.accountmanagementproject.messaging.sse.SseEmitters;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final SseEmitters sseEmitters;

    public NotificationController(NotificationService notificationService, SseEmitters sseEmitters) {
        this.notificationService = notificationService;
        this.sseEmitters = sseEmitters;
    }


    @GetMapping("/subscribe")
    public SseEmitter subscribe(@RequestParam Long userId) {
        return sseEmitters.addEmitter(userId);
    }

    // TODO : 추가 구현 필요 , 현재 기본만 설정함
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestParam Long userId, @RequestBody String message) {
        notificationService.notifyUser(userId, message);
        return ResponseEntity.ok("Notification sent successfully!");
    }
}
