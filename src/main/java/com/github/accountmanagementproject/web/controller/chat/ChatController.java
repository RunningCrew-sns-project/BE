package com.github.accountmanagementproject.web.controller.chat;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.account.users.MyUsersJpa;
import com.github.accountmanagementproject.repository.chat.ChatRoomRepository;
import com.github.accountmanagementproject.service.chat.ChatService;
import com.github.accountmanagementproject.web.dto.chat.ChatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final SimpMessageSendingOperations template;
    private final ChatService chatService;
    private final AccountConfig accountConfig;
    private final MyUsersJpa myUsersJpa;

    @MessageMapping("/chat/enterUser")
    public void enterUser(@Payload ChatDto chat, SimpMessageHeaderAccessor headerAccessor){
        log.info("enter User {}", chat.getSender());
        chatService.increaseUser(chat.getRoomId());
        MyUser user = accountConfig.findMyUser(chat.getSender());

        Integer userID = chatService.addUser(chat.getRoomId(), user);

        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("userID", userID);
        headerAccessor.getSessionAttributes().put("roomID", chat.getRoomId());

        chat.setMessage(chat.getSender() + "님이 입장하셨습니다.");
        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
    }

    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatDto chat){
        log.info("chat : {}", chat);
        chat.setMessage(chat.getMessage());
        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
    }

    @EventListener
    public void handleWebSocketConnectEvent(SessionConnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        log.info("websocket connect event : {}", headerAccessor.getSessionId());
    }
    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event){
        log.info("webSocketDisconnectListener : {}", event);

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Integer userId = (Integer) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("userId");
        Integer roomId = (Integer) headerAccessor.getSessionAttributes().get("roomID");

        MyUser user = myUsersJpa.findById(userId).orElseThrow(null);

        log.info("headerAccessor : {}", headerAccessor);

        chatService.decreaseUser(roomId);
        chatService.deleteUser(roomId, user);

        if(user != null){
            log.info("User disconnected : {}", user.getNickname());

            ChatDto chat = ChatDto.builder()
                    .type(ChatDto.MessageType.LEAVE)
                    .sender(user.getNickname())
                    .message(user.getNickname() + "님이 퇴장하였습니다.")
                    .build();

            template.convertAndSend("/sub/chat/room/" + roomId, chat);
        }
    }

    @GetMapping("/chat/userlist")
    @ResponseBody
    public List<String> userList(Integer roomId){
        return chatService.getUserList(roomId);
    }
}
