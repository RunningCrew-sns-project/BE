package com.github.runningcrewsnsproject.config.client.dto.userInfo;


import com.github.runningcrewsnsproject.repository.account.user.myenum.OAuthProvider;

public interface OAuthUserInfo {
    String getSocialId();
    String getEmail();
    String getNickname();
    String getProfileImg();
    OAuthProvider getOAuthProvider();
}
