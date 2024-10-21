package com.github.accountmanagementproject.web.controller.chat;
import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.service.chat.ChatService;
import com.github.accountmanagementproject.web.dto.chat.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    private static final Logger log = LoggerFactory.getLogger(ChatRoomController.class);
    private final AccountConfig accountConfig;
    private final ChatService chatService;

    @GetMapping
    public String chatRoomList(Model model) {
        model.addAttribute("list", chatService.findAllRoom());
        log.info("Chat room list : {}", chatService.findAllRoom());

        return chatService.findAllRoom();
    }

    @PostMapping("/chat/createRoom")
    public String createRoom(@RequestParam String roomName, RedirectAttributes rttr, @AuthenticationPrincipal String principal){
        MyUser user = accountConfig.findMyUser(principal);
        ChatRoom chatRoom = chatService.createChatRoom(roomName, user);
        log.info("Chat room created: {}", chatRoom);
        return "채팅방 생성 : " + chatRoom.getTitle();
    }

    @GetMapping("/chat/joinRoom")
    public String joinRoom(@RequestParam(name = "roomId") Integer roomId, Model model, @AuthenticationPrincipal String principal){
        log.info("roomId : {}", roomId);
        model.addAttribute("room", chatService.findByRoomId(roomId));
        MyUser user = accountConfig.findMyUser(principal);
        chatService.addUser(roomId, user);
        ChatRoom chatRoom = chatService.findByRoomId(roomId);

        return chatRoom.getTitle() + "에 입장했습니다.";
    }

}
