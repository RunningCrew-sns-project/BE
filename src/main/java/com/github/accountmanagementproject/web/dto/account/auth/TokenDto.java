package com.github.accountmanagementproject.web.dto.account.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private Object userId;
}
