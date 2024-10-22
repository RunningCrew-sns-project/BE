package com.github.accountmanagementproject.web.dto.accountAuth.myPage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.accountmanagementproject.repository.account.users.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "이메일은 필수 입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    @Schema(description = "이메일", example = "abc@abc.com", pattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "비밀번호는 필수 입니다.")
    @Pattern(regexp="^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$",
            message = "비밀번호는 영문과 특수문자 숫자를 포함하며 8자 이상 20자 이하여야 합니다.")
    @Schema(description = "비밀번호 (*영문과 특수문자, 숫자를 포함)", example = "12341234a!", minLength = 8, maxLength = 20, pattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$")
    private String password;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "비밀번호 확인은 필수 입니다.")
    @Schema(description = "비밀번호 확인", example = "12341234a!", minLength = 8, maxLength = 20, pattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$")
    private String passwordConfirm;

    @AssertTrue(message = "비밀번호와 비밀번호 확인이 같아야 합니다.")
    private boolean isPasswordEquals(){
        return this.password.equals(this.passwordConfirm);
    }

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하여야 합니다.")
    @Pattern(regexp = "^(?!01\\d{9}$).*$", message = "핸드폰 번호를 닉네임으로 사용할 수 없습니다.")
    @Schema(description = "닉네임", example = "이브라히모비치",  minLength = 2, maxLength = 8)
    private String nickname;

    @NotBlank(message = "핸드폰 번호는 필수 입니다.")
    @Pattern(regexp = "01\\d{9}", message = "핸드폰 번호는 01로 시작하며 11자리 숫자여야 합니다.")
    @Schema(description = "핸드폰 번호", example = "01012345678", pattern = "01\\d{9}", minLength = 11, maxLength = 11)
    private String phoneNumber;

    @Schema(description = "성별", example = "남성", defaultValue = "미정")
    private Gender gender;

    @Pattern(regexp= "^(19|20)\\d{2}-(0?[1-9]|1[0-2])-(0?[1-9]|[12][0-9]|3[01])$|" +
            "^(19|20)\\d{2}-(02)-(29)$|" +
            "^(19|20)\\d{2}-(0?[1,3-9]|1[0-2])-(30)$|" +
            "^(19|20)\\d{2}-(0?[13578]|1[0,2])-(31)$",
            message = "생년월일은 yyyy-M-d 형식으로 입력해주세요.")
    @Schema(description = "생년월일", example = "1999-1-8", pattern = "^(19|20)\\d{2}-(0?[1-9]|1[0-2])-(0?[1-9]|[12][0-9]|3[01])$|" +
            "^(19|20)\\d{2}-(02)-(29)$|" +
            "^(19|20)\\d{2}-(0?[1,3-9]|1[0-2])-(30)$|" +
            "^(19|20)\\d{2}-(0?[13578]|1[0,2])-(31)$")
    private String dateOfBirth;

    @Pattern(regexp = "^(http(s)?://)[\\w.-]+(/[\\w+.%&=;:@#\\-]*)*\\.(jpg|jpeg|png|gif|bmp|svg|JPG|JPEG|PNG|GIF|BMP|SVG)$",
            message = "프로필 이미지는 이미지 링크의 URL 로 요청을 보내주세요.")
    @Schema(description = "프로필 이미지 (요청 값에 포함 시키지 않을 시 기본 사진이 적용 됩니다.)",
            defaultValue = "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/elderly-man-icon.png",
            example = "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/elderly-man-icon.png", pattern = "^(http(s)?://)[\\w.-]+(/[\\w+.%&=;:@#\\-]*)*\\.(jpg|jpeg|png|gif|bmp|svg|JPG|JPEG|PNG|GIF|BMP|SVG)$")
    private String profileImg;

    @JsonCreator
    public SignUpRequest(@JsonProperty("profileImg") String profileImg) {
        this.profileImg = profileImg!=null?profileImg:defaultProfileUrl();
    }

    public String defaultProfileUrl() {
        if (this.gender == null || this.gender == Gender.UNKNOWN)
            return "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/anonymous-user-icon.png";
        else if (this.gender == Gender.MALE)
            return "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/man-user-color-icon.png";
        else
            return "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/woman-user-color-icon.png";
    }

}
