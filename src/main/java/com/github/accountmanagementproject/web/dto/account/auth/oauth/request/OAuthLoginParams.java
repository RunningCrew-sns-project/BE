package com.github.accountmanagementproject.web.dto.account.auth.oauth.request;

import com.github.accountmanagementproject.repository.account.user.myenum.OAuthProvider;
import org.springframework.util.MultiValueMap;

public interface OAuthLoginParams {
    OAuthProvider oAuthProvider();
    MultiValueMap<String, String> makeBody();
}
