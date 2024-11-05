package com.github.runningcrewsnsproject.web.dto.crew;

import com.github.runningcrewsnsproject.repository.account.user.myenum.Gender;
import com.github.runningcrewsnsproject.repository.crew.crewuser.CrewsUsersStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CrewUserResponse {
    @Schema(description = "유저 이메일", example = "abc@abc.com")
    private String email;
    @Schema(description = "유저 이름(닉네임)", example = "이시후")
    private String nickname;
    @Schema(description = "유저 이미지 URL", example = "https://runningcrewsnsproject.s3.ap-northeast-2.amazonaws.com/user/1.jpg")
    private String userImageUrl;
    @Schema(description = "유저 프로필 메시지", example = "달리고 싶다..⭐")
    private String profileMessage;
    @Schema(description = "유저 성별", example = "남성")
    private Gender gender;
    @Schema(description = "유저 마지막 로그인 날짜", example = "2021-08-01T00:00:00")
    private LocalDateTime lastLoginDate;
    @Schema(description = "크루에 대한 유저의 상태 ", example = "가입 요청")
    private CrewsUsersStatus status;
    @Schema(description = "받은 경고횟수", example = "1")
    private int caveat;
    @Schema(description = "유저 가입신청 또는 가입 날짜", example = "2021-08-01T00:00:00")
    private LocalDateTime joinRequestOrJoinDate;

}
