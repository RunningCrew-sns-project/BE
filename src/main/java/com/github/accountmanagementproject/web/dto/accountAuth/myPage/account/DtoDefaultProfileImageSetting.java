package com.github.accountmanagementproject.web.dto.accountAuth.myPage.account;

import com.github.accountmanagementproject.repository.account.users.enums.Gender;

public interface DtoDefaultProfileImageSetting {
    default String defaultProfileUrl(Gender gender) {
        if (gender == null || gender == Gender.UNKNOWN)
            return "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/anonymous-user-icon.png";
        else if (gender == Gender.MALE)
            return "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/man-user-color-icon.png";
        else
            return "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/woman-user-color-icon.png";
    }
}
