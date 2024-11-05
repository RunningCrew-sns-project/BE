package com.github.runningcrewsnsproject.config.client;

import com.github.runningcrewsnsproject.config.client.dto.userInfo.OAuthUserInfo;
import com.github.runningcrewsnsproject.repository.account.user.myenum.OAuthProvider;
import com.github.runningcrewsnsproject.web.dto.account.auth.oauth.request.OAuthLoginParams;

public interface OAuthApiClient {
    OAuthProvider oAuthProvider();
    String requestAccessToken(OAuthLoginParams params);
    OAuthUserInfo requestOauthInfo(String accessToken);
}
