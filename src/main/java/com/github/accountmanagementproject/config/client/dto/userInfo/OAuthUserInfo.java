package com.github.accountmanagementproject.config.client.dto.userInfo;


import com.github.accountmanagementproject.repository.account.user.myenum.OAuthProvider;

public interface OAuthUserInfo {
    String getSocialId();
    String getEmail();
    String getNickname();
    String getProfileImg();
    OAuthProvider getOAuthProvider();
}
