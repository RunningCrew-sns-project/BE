package com.github.accountmanagementproject.web.controller.chat;

import com.github.accountmanagementproject.web.dto.chat.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    //클라이언트가 /app/chat.sendMessage로 보낸 메세지를 처리
    @MessageMapping("/chat.sendMessage")
    //해당 메세지를 구독중인 클라이언트에게 전송
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage message){
        return message;
    }

    //클라이언트가 /app/chat.addUser로 보낸 메세지 처리
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(ChatMessage message, SimpMessageHeaderAccessor headerAccessor){
        //WebSocket 세션에 사용자 이름 저장
        headerAccessor.getSessionAttributes().put("username", message.getSender());
        return message;
    }
}
