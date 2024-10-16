package com.github.accountmanagementproject.web.controller.chat;

import com.github.accountmanagementproject.service.chat.ChatRoomService;
import com.github.accountmanagementproject.web.dto.chat.ChatMessage;
import com.github.accountmanagementproject.web.dto.chat.RequestChatRoomDto;
import com.github.accountmanagementproject.web.dto.chat.ResponseChatRoomDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessageSendingOperations template;
    private final ChatRoomService chatRoomService;


    @GetMapping("/chat/{id}")
    public ResponseEntity<List<ChatMessage>> getChatMessages(@PathVariable("id") String id) {
        ChatMessage chatMessage = new ChatMessage(1, "test", "test");
        return ResponseEntity.ok(List.of(chatMessage));
    }

    //메시지 송신 및 수신, /pub가 생략된 모습. 클라이언트 단에선 /pub/message로 요청
    @MessageMapping("/message")
    public ResponseEntity<Void> receiveMessage(@RequestBody ChatMessage chat) {
        // 메시지를 해당 채팅방 구독자들에게 전송
        template.convertAndSend("/sub/chatroom/1", chat);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseChatRoomDto> createChatRoom(@RequestBody RequestChatRoomDto requestChatRoomDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatRoomService.createChatRoom(requestChatRoomDto));
    }

    @GetMapping("/chatList")
    public ResponseEntity<List<ResponseChatRoomDto>> getChatRoomList() {
        List<ResponseChatRoomDto> responseChatRoomDtoList = chatRoomService.findChatRoomList();
        return ResponseEntity.ok(responseChatRoomDtoList);
    }
}
