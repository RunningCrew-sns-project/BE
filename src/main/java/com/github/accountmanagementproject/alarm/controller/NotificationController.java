package com.github.accountmanagementproject.alarm.controller;


import com.github.accountmanagementproject.alarm.sse.SseEmitters;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Tag(name = "Notifications", description = "알림 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(originPatterns = "*")
public class NotificationController {

    private final SseEmitters sseEmitters;


    /**
     * 클라이언트와 SSE 연결을 설정하고 SseEmitter 반환
     * 클라이언트는 이 엔드포인트를 구독하여 실시간 알림을 수신합니다.
     * @param userId 알림을 수신할 사용자 ID
     * @return SseEmitter 연결 객체
     */
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
    public SseEmitter connect(
            @Parameter(description = "알림을 수신할 사용자 ID", example = "123")
            @RequestParam Long userId
    ) {
        return sseEmitters.addEmitter(userId);
    }


}
