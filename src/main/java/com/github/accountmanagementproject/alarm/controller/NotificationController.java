package com.github.accountmanagementproject.alarm.controller;


import com.github.accountmanagementproject.alarm.sse.SseEmitters;
import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.exception.SimpleRunAppException;
import com.github.accountmanagementproject.exception.enums.ErrorCode;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(originPatterns = "*")
public class NotificationController implements NotificationControllerDocs{

    private final SseEmitters sseEmitters;
    private final AccountConfig accountConfig;
    private final MyUsersRepository userRepository;


    @CrossOrigin(
            origins = {"http://localhost:8080", "http://54.180.9.220:8080"},
            allowedHeaders = "*",
            allowCredentials = "true"
    )
    @GetMapping("/connect")
    @Override
    public ResponseEntity<SseEmitter> connect(@AuthenticationPrincipal String email) {
//        MyUser user = accountConfig.findMyUser(email);
        MyUser admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND));


        SseEmitter emitter = sseEmitters.addEmitter(admin.getUserId());

        // 이벤트 전송
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("Connected successfully"));
        } catch (IOException e) {
            sseEmitters.removeEmitter(admin.getUserId());
        }

        // ResponseEntity를 통해 헤더 설정
        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache")
                .header("Connection", "keep-alive")
                .body(emitter);
    }


}
