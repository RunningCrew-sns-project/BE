package com.github.accountmanagementproject.web.dto.accountAuth.oauth.client;

import com.github.accountmanagementproject.repository.account.users.enums.OAuthProvider;
import org.springframework.util.MultiValueMap;

public interface OAuthLoginParams {
    OAuthProvider oAuthProvider();
    MultiValueMap<String, String> makeBody();
}
