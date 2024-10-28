package com.github.accountmanagementproject.web.dto.accountAuth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.accountmanagementproject.repository.account.users.enums.Gender;
import com.github.accountmanagementproject.web.dto.accountAuth.myPage.AccountInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest extends AccountInfoDto {


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



    @JsonCreator
    public SignUpRequest(@JsonProperty("profileImg") String profileImg) {
        super.setProfileImg( profileImg!=null?profileImg:defaultProfileUrl() );
    }

    public String defaultProfileUrl() {
        if (super.getGender() == null || super.getGender() == Gender.UNKNOWN)
            return "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/anonymous-user-icon.png";
        else if (super.getGender() == Gender.MALE)
            return "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/man-user-color-icon.png";
        else
            return "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/woman-user-color-icon.png";
    }
    public void passwordEncryption(String password){
        this.password = password;
    }

}
