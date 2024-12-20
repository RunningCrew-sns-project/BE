package com.github.accountmanagementproject.web.dto.account.auth.oauth.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.accountmanagementproject.repository.account.user.myenum.OAuthProvider;
import com.github.accountmanagementproject.web.dto.account.mypage.AccountModifyRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OAuthSignUpRequest extends AccountModifyRequest {
    @NotBlank(message = "소셜 식별자 값은 필수입니다.")
    @Schema(description = "소셜 아이디", example = "Z9Vp6uyQ1S03CtxKHCnFS80KItHrRxIuwWse12EIupw")
    private String socialId;
    @NotNull(message = "소셜 공급자 값은 필수입니다.")
    private OAuthProvider provider;
}
