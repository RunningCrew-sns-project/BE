package com.github.accountmanagementproject.repository.account.user.myenum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OAuthProvider implements MyEnumInterface{
    KAKAO("카카오"), NAVER("네이버"), GOOGLE("구글");

    private final String value;
}
