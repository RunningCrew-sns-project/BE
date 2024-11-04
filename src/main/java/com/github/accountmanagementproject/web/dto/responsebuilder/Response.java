package com.github.accountmanagementproject.web.dto.responsebuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class Response<T> {
    private final String resultCode;
    private final int code;
    private final String httpStatus;
    private final String message;
    private final String detailMessage;  // 상세 메시지 필드 추가
    private final T responseData;
    private final LocalDateTime timestamp;

    private Response(String resultCode, int code, String httpStatus, String message, String detailMessage, T responseData) {
        this.resultCode = resultCode;
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
        this.detailMessage = detailMessage;
        this.responseData = responseData;
        this.timestamp = LocalDateTime.now();
    }


    public static <T> Response<T> success(String message, T responseData) {
        return success(HttpStatus.CREATED, message, responseData);
    }

    public static <T> Response<T> success(HttpStatus status, String message, T responseData) {
        return new Response<>("success", status.value(), status.name(), message, null, responseData);
    }

    // 에러 응답 생성 메서드
    public static <T> Response<T> error(HttpStatus status, String message, String detailMessage) {
        return new Response<>("error", status.value(), status.name(),
                message, detailMessage, null);
    }
}
