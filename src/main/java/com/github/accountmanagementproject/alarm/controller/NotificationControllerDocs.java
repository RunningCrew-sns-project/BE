package com.github.accountmanagementproject.alarm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Tag(name = "Notifications", description = "알림 관련 API")
public interface NotificationControllerDocs {

    @Operation(
            summary = "SSE 연결 설정",
            description = "이 엔드포인트를 호출하면 서버와의 SSE 연결을 설정하여 실시간 알림을 수신할 수 있습니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "SSE 연결 성공",
                            content = @Content(mediaType = "text/event-stream",
                                    examples = @ExampleObject(name = "SSE 연결 예시", value = """
                        event: notification
                        data: {"message": "새로운 알림이 있습니다"}
                    """)
                            )
                    )
            }
    )
    @GetMapping("/connect")
    ResponseEntity<SseEmitter> connect(@RequestParam Long userId);


}
