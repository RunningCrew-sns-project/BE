package com.github.accountmanagementproject.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //stomp 접속 url -> ws://localhost:8080/ws

        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
        log.info("Client Connected");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //메세지를 구독하는 요청 -> 메세지 받을 때
        registry.enableSimpleBroker("/sub");

        //메세지 송신하는 요청 -> 메세지 보낼 때
        registry.setApplicationDestinationPrefixes("/pub");
    }

}
