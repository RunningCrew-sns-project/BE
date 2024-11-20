package com.github.accountmanagementproject.web.controller.chat;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.exception.CustomNotFoundException;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.repository.chat.ChatMongoRepository;
import com.github.accountmanagementproject.repository.chat.UserChatMappingRepository;
import com.github.accountmanagementproject.service.chat.ChatService;
import com.github.accountmanagementproject.web.dto.chat.ChatDto;
import com.github.accountmanagementproject.web.dto.chat.ChatMongoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
//https://khdscor.tistory.com/122 참고
public class ChatController {
    private final SimpMessageSendingOperations template;
    private final ChatService chatService;
    private final AccountConfig accountConfig;
    private final MyUsersRepository myUsersJpa;
    private final UserChatMappingRepository userChatMappingRepository;
    private final ChatMongoRepository chatMongoRepository;


    //클라이언트에서 /pub/chat/enterUser로 입장할때 입장 메세지 담아서 요청 보내면 여기서 처리
    @MessageMapping("/chat/enterUser")
    public void enterUser(
            @Payload ChatDto chat,
            StompHeaderAccessor headerAccessor){

        log.info("header : {}", headerAccessor.getUser().getName());
        MyUser user = accountConfig.findMyUser(headerAccessor.getUser().getName());

//        log.info("enter User {}", principal);

        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("userID", user.getUserId());
        headerAccessor.getSessionAttributes().put("roomID", chat.getRoomId());

        if(!chatService.addUser(chat.getRoomId(), user)){
            chat.setMessage(user.getNickname() + "님이 입장하셨습니다. ");
            chat.setUserName(user.getNickname());
            chat.setTime(chat.getTime().plusHours(9));
            template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
            chatMongoRepository.save(ChatMongoMapper.INSTANCE.chatDtoToChatMongoDto(chat, user.getEmail()));
        }
    }

    //클라이언트에서 /pub/chat/sendMessage로 ChatDto의 형태로 담아서 보내면 여기서 처리
    @MessageMapping("/chat/sendMessage")
    public void sendMessage(
            @Payload ChatDto chat,
            @AuthenticationPrincipal String principal,
            StompHeaderAccessor headerAccessor){

        log.info("chat : {}", chat);
        log.info("principal : {}", principal);
        log.info("header : {}", headerAccessor.getUser().getName());
        MyUser user = accountConfig.findMyUser(headerAccessor.getUser().getName());
        chat.setUserName(user.getNickname());
        chat.setTime(chat.getTime().plusHours(9));
        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
        chatMongoRepository.save(ChatMongoMapper.INSTANCE.chatDtoToChatMongoDto(chat, user.getEmail()));
    }

    //클라이언트에서 /pub/chat/leaveUser 로 요청보내면 여기서 처리
    @MessageMapping("/chat/leaveUser")
    public void leaveUser(
            @Payload ChatDto chat,
            @AuthenticationPrincipal String principal,
            StompHeaderAccessor headerAccessor){

        log.info("chat : {}", chat);
        log.info("principal : {}", principal);
        log.info("header : {}", headerAccessor.getUser().getName());

        MyUser user = accountConfig.findMyUser(headerAccessor.getUser().getName());
        chatService.deleteUser(chat.getRoomId(), user);

        chat.setMessage(user.getNickname() + "님이 퇴장하셨습니다. ");
        chat.setUserName(user.getNickname());
        chat.setTime(chat.getTime().plusHours(9));
        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
        chatMongoRepository.save(ChatMongoMapper.INSTANCE.chatDtoToChatMongoDto(chat, user.getEmail()));
    }

    @EventListener
    public void handleWebSocketConnectEvent(SessionConnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        MyUser user = accountConfig.findMyUser(headerAccessor.getUser().getName());
        headerAccessor.setUser(event.getUser());
        headerAccessor.getSessionAttributes().put("userID", user.getUserId());

        log.info("websocket connect event : {}", headerAccessor.getSessionId());
    }
    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event){
        log.info("webSocketDisconnectListener : {}", event);

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("이벤트 :!!!! " + event.getMessage().toString());
        log.info(headerAccessor.toString());
//
//        Integer userId = (Integer) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("userID");
//        Integer roomId = (Integer) headerAccessor.getSessionAttributes().get("roomID");
//
//        log.warn(userId + "유저아이디!!!!!!!!!!!!!1");
//
//        MyUser user = myUsersJpa.findById(userId).orElseThrow(()->new CustomNotFoundException.ExceptionBuilder()
//                .customMessage("유저를 찾을 수 없습니다").build());
//
//        log.info("headerAccessor : {}", headerAccessor);
//        if(user != null){
//            log.info("User disconnected : {}", user.getNickname());
//        }
    }
}
