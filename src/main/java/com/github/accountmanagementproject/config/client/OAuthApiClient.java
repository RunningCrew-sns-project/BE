package com.github.accountmanagementproject.config.client;

import com.github.accountmanagementproject.config.client.dto.userInfo.OAuthUserInfo;
import com.github.accountmanagementproject.repository.account.user.myenum.OAuthProvider;
import com.github.accountmanagementproject.web.dto.account.auth.oauth.request.OAuthLoginParams;

public interface OAuthApiClient {
    OAuthProvider oAuthProvider();
    String requestAccessToken(OAuthLoginParams params);
    OAuthUserInfo requestOauthInfo(String accessToken);
}
