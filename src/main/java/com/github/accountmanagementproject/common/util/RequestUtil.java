package com.github.accountmanagementproject.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@RequiredArgsConstructor
@Slf4j
public class RequestUtil {

    public static Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return null;
        }

        try {
            log.info("authentication.getName() : " + authentication.getName());
            // Security Context에서 사용자 이메일 가져오기

            String email = authentication.getName(); // 사용자의 이메일을 가져옴


//            return
            return Long.parseLong(authentication.getName());  // 이 부분 수정 예정
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
