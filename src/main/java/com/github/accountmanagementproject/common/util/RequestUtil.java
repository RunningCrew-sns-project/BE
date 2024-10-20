package com.github.accountmanagementproject.common.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@Slf4j
public class RequestUtil {

    public static Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return null;
        }

        try {
            log.info("authentication.getName() : " + authentication.getName());
            return Long.parseLong(authentication.getName());  // 이 부분 수정 예정
        } catch (NumberFormatException e) {
            return null;
        }
    }

}