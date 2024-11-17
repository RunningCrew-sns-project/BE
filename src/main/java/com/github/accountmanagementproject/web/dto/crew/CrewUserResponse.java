package com.github.accountmanagementproject.web.dto.crew;

import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CrewUserResponse extends CrewUserParent {
    @Schema(description = "유저 이메일", example = "abc@abc.com")
    private String email;
    @Schema(description = "유저 마지막 로그인 날짜", example = "2021-08-01T00:00:00")
    private LocalDateTime lastLoginDate;
    @Schema(description = "크루에 대한 유저의 상태 ", example = "가입 요청")
    private CrewsUsersStatus status;
    @Schema(description = "받은 경고횟수", example = "1")
    private int caveat;
    @Schema(description = "유저 가입신청 또는 가입 날짜", example = "2021-08-01T00:00:00")
    private LocalDateTime joinRequestOrJoinDate;
    @Schema(description = "유저 고유 아이디", example = "22")
    private Long userId;

}
