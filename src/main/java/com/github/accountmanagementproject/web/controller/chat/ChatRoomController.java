package com.github.accountmanagementproject.web.controller.chat;
import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.chat.ChatRepository;
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
    private final ChatRepository repository;
    private final AccountConfig accountConfig;

    @GetMapping("/")
    public String chatRoomList(Model model) {
        model.addAttribute("list", repository.findAllRoom());
        log.info("Chat room list : {}", repository.findAllRoom());

        return repository.findAllRoom().toString();
    }

    @PostMapping("/chat/createRoom")
    public String createRoom(@RequestParam String roomName, RedirectAttributes rttr){
        ChatRoom chatRoom = repository.createChatRoom(roomName);
        log.info("Chat room created: {}", chatRoom);

        rttr.addFlashAttribute("roomName", chatRoom);

        return "redirect:/";
    }

    @GetMapping("/chat/joinRoom")
    public String joinRoom(@RequestParam(name = "roomId") String roomId, Model model, @AuthenticationPrincipal String principal){
        log.info("roomId : {}", roomId);
        model.addAttribute("room", repository.findByRoomId(roomId));
        MyUser user = accountConfig.findMyUser(principal);
        repository.addUser(roomId, user.getNickname());
        ChatRoom chatRoom = repository.findByRoomId(roomId);

        return chatRoom.getRoomName() + "에 입장했습니다.";
    }

}
