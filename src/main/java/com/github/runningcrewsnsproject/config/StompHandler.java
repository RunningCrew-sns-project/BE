package com.github.runningcrewsnsproject.config;

import com.github.runningcrewsnsproject.config.security.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
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

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    private static final Logger log = LoggerFactory.getLogger(StompHandler.class);
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if(!StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }
        Optional<String> token = Optional.ofNullable(accessor.getFirstNativeHeader(AUTHORIZATION_HEADER));

        String jwtToken = token
                .filter(t->t.startsWith(BEARER_PREFIX))
                .map(t->t.substring(BEARER_PREFIX.length()))
                .filter(this::validateAccessToken)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        Authentication auth = jwtProvider.getAuthentication(jwtToken);
        accessor.setUser(auth);

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
