package com.github.runningcrewsnsproject.web.dto.account.mypage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.runningcrewsnsproject.repository.account.user.myenum.RolesEnum;
import com.github.runningcrewsnsproject.repository.account.user.myenum.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class AccountInfoResponse extends AccountInfoDto{

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "마지막 로그인 날짜")
    private String lastLogin;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "계정 상태")
    private UserStatus status;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "유저의 허용 권한")
    private Set<RolesEnum> roles;

}
