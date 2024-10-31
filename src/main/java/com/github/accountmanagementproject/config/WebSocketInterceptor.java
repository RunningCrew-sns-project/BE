package com.github.accountmanagementproject.config;

import com.github.accountmanagementproject.config.security.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class WebSocketInterceptor implements ChannelInterceptor {
    private static final Logger log = LoggerFactory.getLogger(WebSocketInterceptor.class);
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor.getCommand() == StompCommand.CONNECT) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7).trim();
            }

            if (this.validateAccessToken(token)) {
                Authentication authentication = jwtProvider.getAuthentication(token);
                accessor.setUser(authentication);  // 세션에 인증 정보 설정
                log.info("accessor user : " + String.valueOf(accessor.getUser()));
            }
        }
        return message;
    }

    private boolean validateAccessToken(String accessToken) {
        if (accessToken == null) {
            return false;
        }

        String bearerToken = accessToken.trim();

        if (!bearerToken.trim().isEmpty() && bearerToken.startsWith("Bearer ")) {
            accessToken = bearerToken.substring(7);
        }
        try {
            Claims claims = jwtProvider.tokenParsing(accessToken).getPayload();
            return true;
        } catch (ExpiredJwtException | MalformedJwtException e) {
            return false;
        }

    }
}
