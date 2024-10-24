package com.github.accountmanagementproject.web.dto.accountAuth.myPage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.accountmanagementproject.repository.account.users.enums.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountModifyRequest extends AccountInfoDto{
    private String email;
    private String nickname;
    private String phoneNumber;
    private Gender gender;
    private String profileImg;
    @JsonCreator
    public AccountModifyRequest(@JsonProperty("profileImg") String profileImg) {
        if(profileImg == null) return;
        this.profileImg = !profileImg.isEmpty() ? profileImg:defaultProfileUrl() ;
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
