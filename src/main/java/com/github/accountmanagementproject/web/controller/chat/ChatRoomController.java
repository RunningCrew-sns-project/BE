package com.github.accountmanagementproject.web.controller.chat;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.service.chat.ChatService;
import com.github.accountmanagementproject.web.dto.chat.ChatRoom;
import com.github.accountmanagementproject.web.dto.chat.ChatRoomResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat/")
@RequiredArgsConstructor
public class ChatRoomController implements ChatRoomControllerDocs{
    private static final Logger log = LoggerFactory.getLogger(ChatRoomController.class);
    private final AccountConfig accountConfig;
    private final ChatService chatService;

    @Override
    @GetMapping("/myRooms")
    public List<ChatRoomResponse> myChatRoomList(@AuthenticationPrincipal String principal) {
        log.info(principal + "사용자 인증 정보!!!!!");
        MyUser user = accountConfig.findMyUser(principal);
        return chatService.findMyRoomList(user);
    }

    @Override
    @PostMapping("/createRoom")
    public String createRoom(@RequestBody String roomName, @AuthenticationPrincipal String principal){
        MyUser user = accountConfig.findMyUser(principal);
        ChatRoom chatRoom = chatService.createChatRoom(roomName, user);
        log.info("Chat room created: {}", chatRoom);
        return "채팅방 생성 : " + chatRoom.getTitle();
    }

    @Override
    @GetMapping("/userlist/{roomId}")
    @ResponseBody
    public List<String> userList(@PathVariable Integer roomId){
        return chatService.getUserList(roomId);
    }

    @Override
    @GetMapping("/message")
    public ResponseEntity<?> getMessageByRoomId(
            @AuthenticationPrincipal String principal,
            @RequestParam Integer roomId,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> lastTime){
        log.info(principal + "사용자 인증 정보!!!!!");
        MyUser user = accountConfig.findMyUser(principal);
        return ResponseEntity.ok(chatService.getMessageByRoomId(roomId, user, limit, lastTime));
    }

}
