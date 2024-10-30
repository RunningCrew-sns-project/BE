package com.github.accountmanagementproject.web.controller.chat;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.repository.account.users.MyUsersJpa;
import com.github.accountmanagementproject.repository.chat.UserChatMappingRepository;
import com.github.accountmanagementproject.service.chat.ChatService;
import com.github.accountmanagementproject.web.dto.chat.ChatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final SimpMessageSendingOperations template;
    private final ChatService chatService;
    private final AccountConfig accountConfig;
<<<<<<< HEAD
    private final MyUsersRepository myUsersJpa;
=======
    private final MyUsersJpa myUsersJpa;
    private final UserChatMappingRepository userChatMappingRepository;

>>>>>>> ff54ada7d92f3600472cd04a888b9ec26b52217d

    @MessageMapping("/chat/enterUser")
    public void enterUser(@Payload ChatDto chat, StompHeaderAccessor headerAccessor){

        log.info("header accessor : " + headerAccessor.getUser());
        log.info("user : " + headerAccessor.getUser().getName());

        MyUser user = accountConfig.findMyUser(headerAccessor.getUser().getName());

//        log.info("enter User {}", principal);

        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("userID", user.getUserId());
        headerAccessor.getSessionAttributes().put("roomID", chat.getRoomId());

        if(!chatService.addUser(chat.getRoomId(), user)){
            chat.setType(ChatDto.MessageType.ENTER);
            chat.setSender(user.getEmail());
//        chat.setUserName(user.getNickname());
            chat.setMessage(user.getEmail() + "님이 입장하셨습니다. " + changeTime(chat.getTime()));
            template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
        }
    }

    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatDto chat, StompHeaderAccessor headerAccessor){
        log.info("chat : {}", chat);
        MyUser user = accountConfig.findMyUser(headerAccessor.getUser().getName());
        chat.setType(ChatDto.MessageType.TALK);
        chat.setSender(user.getEmail());
//        chat.setUserName(user.getNickname());
        chat.setMessage(user.getEmail() + " : " + chat.getMessage() + " " + changeTime(chat.getTime()));
        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
    }

    @MessageMapping("/chat/leaveUser")
    public void leaveUser(@Payload ChatDto chat, StompHeaderAccessor headerAccessor){
        log.info("chat : {}", chat);
        MyUser user = accountConfig.findMyUser(headerAccessor.getUser().getName());
        chatService.deleteUser(chat.getRoomId(), user);
        chat.setType(ChatDto.MessageType.LEAVE);
        chat.setSender(user.getEmail());
//        chat.setUserName(user.getNickname());
        chat.setMessage(user.getEmail() + "님이 퇴장하셨습니다. " + changeTime(chat.getTime()));
        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
    }

    @EventListener
    public void handleWebSocketConnectEvent(SessionConnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        headerAccessor.setUser(event.getUser());

        log.info("websocket connect event : {}", headerAccessor.getSessionId());
    }
    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event){
        log.info("webSocketDisconnectListener : {}", event);

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("이벤트 :!!!! " + event.getMessage().toString());
        log.info(headerAccessor.toString());

        Integer userId = (Integer) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("userID");
        Integer roomId = (Integer) headerAccessor.getSessionAttributes().get("roomID");

        MyUser user = myUsersJpa.findById(userId).orElseThrow(null);

        log.info("headerAccessor : {}", headerAccessor);
        if(user != null){
            log.info("User disconnected : {}", user.getNickname());

//            chatService.deleteUser(roomId, user);

//            ChatDto chat = ChatDto.builder()
//                    .type(ChatDto.MessageType.LEAVE)
//                    .sender(user.getNickname())
//                    .message(user.getNickname() + "님이 퇴장하였습니다.")
//                    .build();
//
//            template.convertAndSend("/sub/chat/room/" + roomId, chat);
        }
    }

    private String changeTime(String localDateTime){
        OffsetDateTime utcDateTime = OffsetDateTime.parse(localDateTime);

        ZoneId userZone = ZoneId.of("Asia/Seoul");
        ZonedDateTime userZoneDateTime = utcDateTime.atZoneSameInstant(userZone);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        return userZoneDateTime.format(formatter);

    }
}
