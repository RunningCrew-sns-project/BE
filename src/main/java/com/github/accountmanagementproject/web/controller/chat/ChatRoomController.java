package com.github.accountmanagementproject.web.controller.chat;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.service.chat.ChatService;
import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat/")
@RequiredArgsConstructor
public class ChatRoomController implements ChatRoomControllerDocs{
    private static final Logger log = LoggerFactory.getLogger(ChatRoomController.class);
    private final AccountConfig accountConfig;
    private final ChatService chatService;

    @Override
    @GetMapping("/allRooms")
    public CustomSuccessResponse findAllRoomList(@RequestParam(defaultValue = "10") Integer size,
                                                 @RequestParam(required = false) Integer cursor,
                                                 @AuthenticationPrincipal String email){
        return new CustomSuccessResponse.SuccessDetail()
                .message("채팅룸을 조회했습니다.")
                .responseData(chatService.findAllRoom(size, cursor))
                .build();

    }
    @Override
    @GetMapping("/myRooms")
    public CustomSuccessResponse myChatRoomList(@AuthenticationPrincipal String principal) {
        MyUser user = accountConfig.findMyUser(principal);
        return new CustomSuccessResponse.SuccessDetail()
                .message("사용자가 참여한 채팅방을 조회했습니다.")
                .responseData(chatService.findMyRoomList(user))
                .build();

    }

    @Override
    @PostMapping("/createRoom")
    public CustomSuccessResponse createRoom(@RequestBody String roomName, @AuthenticationPrincipal String principal){
        log.info(principal);
        MyUser user = accountConfig.findMyUser(principal);

        return new CustomSuccessResponse.SuccessDetail()
                .message("채팅방 생성을 완료하였습니다.")
                .responseData(chatService.createChatRoom(roomName, user))
                .build();
    }

    @Override
    @GetMapping("/userlist/{roomId}")
    @ResponseBody
    public CustomSuccessResponse userList(@PathVariable Integer roomId){
        return new CustomSuccessResponse.SuccessDetail()
                .message("사용자 정보를 불러왔습니다.")
                .responseData(chatService.getUserList(roomId))
                .build();
    }

    @Override
    @GetMapping("/message")
    public CustomSuccessResponse getMessageByRoomId(
            @AuthenticationPrincipal String principal,
            @RequestParam Integer roomId,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> lastTime){
        MyUser user = accountConfig.findMyUser(principal);
        return new CustomSuccessResponse.SuccessDetail()
                .message("메세지를 불러왔습니다.")
                .responseData(chatService.getMessageByRoomId(roomId, user, limit, lastTime))
                .build();
    }

}
