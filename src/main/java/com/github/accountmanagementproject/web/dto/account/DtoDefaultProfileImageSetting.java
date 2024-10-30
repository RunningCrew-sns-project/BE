package com.github.accountmanagementproject.web.dto.account;

import com.github.accountmanagementproject.repository.account.user.myenum.Gender;

public interface DtoDefaultProfileImageSetting {

    default void defaultProfileUrlSetting(Gender gender) {
        if (getProfileImg() == null) {
            if (gender == null || gender == Gender.UNKNOWN)
                setProfileImg("https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/anonymous-user-icon.png");
            else if (gender == Gender.MALE)
                setProfileImg("https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/man-user-color-icon.png");
            else
                setProfileImg("https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/woman-user-color-icon.png");
        }
    }

    String getProfileImg();

    void setProfileImg(String profileImg);
}
