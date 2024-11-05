package com.github.accountmanagementproject.web.dto.responsebuilder;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;


@AllArgsConstructor
@Getter
public class ErrorResponse {

    private final int code;
    private final String httpStatus;
    private final String message;
    private final LocalDateTime timestamp;
}
