package com.github.accountmanagementproject.web.dto.crew;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class SimplyCrewUserResponse extends CrewUserParent {

    @Schema(description = "크루에 대한 유저의 상태 ", example = "가입 요청")
    private CrewsUsersStatus status;
    @Schema(description = "유저 가입신청 또는 가입 날짜", example = "2021-08-01T00:00:00")
    private LocalDateTime applicationDate;
    @Schema(description = "유저 고유 아이디", example = "22")
    private Long userId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long crewId;
}
