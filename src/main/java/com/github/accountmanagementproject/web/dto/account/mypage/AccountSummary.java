package com.github.accountmanagementproject.web.dto.account.mypage;

import com.github.accountmanagementproject.web.dto.account.DtoDefaultProfileImageSetting;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountSummary implements DtoDefaultProfileImageSetting {
    @Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하여야 합니다.")
    @Schema(description = "닉네임(최소 2글자에서 최대 8글자, null이면 무시됩니다.)", example = "이브라히모비치",  minLength = 2, maxLength = 8)
    private String nickname;
    @Pattern(regexp = "^(http(s)?://)[\\w.-]+(/[\\w+.%&=;:@#\\-]*)*\\.(jpg|jpeg|png|gif|bmp|svg|JPG|JPEG|PNG|GIF|BMP|SVG)$",
            message = "프로필 이미지는 이미지 링크의 URL 로 요청을 보내주세요.")
    @Schema(description = "프로필 이미지 (요청 값에 포함 시키지 않을 시 기본 사진이 적용 됩니다.)",
            defaultValue = "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/elderly-man-icon.png",
            example = "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/elderly-man-icon.png", pattern = "^(http(s)?://)[\\w.-]+(/[\\w+.%&=;:@#\\-]*)*\\.(jpg|jpeg|png|gif|bmp|svg|JPG|JPEG|PNG|GIF|BMP|SVG)$")
    private String profileImg;
    @Schema(description = "프로필 메시지 (null 이면 상메가 삭제됩니다.)", example = "상메")
    private String profileMessage;
}
