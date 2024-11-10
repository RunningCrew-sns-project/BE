package com.github.accountmanagementproject.alarm.controller;


import com.github.accountmanagementproject.alarm.sse.SseEmitters;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(originPatterns = "*")
public class NotificationController {

    private final SseEmitters sseEmitters;


    @GetMapping("/connect")
    public SseEmitter connect(@RequestParam Long userId) {
        return sseEmitters.addEmitter(userId);
    }


}
