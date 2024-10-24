package com.github.accountmanagementproject.web.dto.accountAuth.myPage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.accountmanagementproject.repository.account.users.enums.Gender;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountModifyRequest extends AccountInfoDto{
//    @Email(message = "이메일 형식이 아닙니다.")
    private String email;
    private String nickname;
    @Null(message = "핸드폰 번호는 01로 시작하며 11자리 숫자여야 합니다.")
    @Pattern(regexp = "01\\d{9}", message = "핸드폰 번호는 01로 시작하며 11자리 숫자여야 합니다.")
    private String phoneNumber;


    @JsonCreator
    public AccountModifyRequest(@JsonProperty("profileImg") String profileImg) {
        if(profileImg == null) return;
        super.setProfileImg( !profileImg.isEmpty() ? profileImg:defaultProfileUrl() );
    }
    public String defaultProfileUrl() {
        if (super.getGender() == null || super.getGender() == Gender.UNKNOWN)
            return "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/anonymous-user-icon.png";
        else if (super.getGender() == Gender.MALE)
            return "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/man-user-color-icon.png";
        else
            return "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/woman-user-color-icon.png";
    }
}
