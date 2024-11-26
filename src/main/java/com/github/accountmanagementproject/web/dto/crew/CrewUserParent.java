package com.github.accountmanagementproject.web.dto.crew;

import com.github.accountmanagementproject.repository.account.user.myenum.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrewUserParent {

    @Schema(description = "유저 이름(닉네임)", example = "이시후")
    private String nickname;
    @Schema(description = "유저 이미지 URL", example = "https://runningcrewsnsproject.s3.ap-northeast-2.amazonaws.com/user/1.jpg")
    private String userImageUrl;
    @Schema(description = "유저 프로필 메시지", example = "달리고 싶다..⭐")
    private String profileMessage;
    @Schema(description = "유저 성별", example = "남성")
    private Gender gender;
}
