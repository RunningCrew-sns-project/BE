package com.github.accountmanagementproject.web.controller.chat;

import com.github.accountmanagementproject.web.dto.chat.ChatRoomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatRoomControllerDocs {

    @Operation(summary = "참여중인 채팅방 불러오기", description = "유저가 현재 참여하고 있는 채팅방 조회")
    @GetMapping("/myRooms")
    List<ChatRoomResponse> myChatRoomList(@AuthenticationPrincipal String principal);

    @Operation(summary = "채팅방 생성하기", description = "채팅방 이름으로 채팅방 생성하기")
    @Parameter(name = "roomName", description = "채팅방 이름")
    @PostMapping("/createRoom")
    String createRoom(@RequestBody String roomName, @AuthenticationPrincipal String principal);

    @Operation(summary = "채팅방에 참여중인 유저 목록 불러오기", description = "roomId를 받아 채팅방에 참여중인 유저 목록 조회")
    @Parameter(name = "roomId", description = "채팅방 아이디")
    @GetMapping("/userlist/{roomId}")
    @ResponseBody
    List<String> userList(@PathVariable Integer roomId);

    @Operation(summary = "채팅방 메세지 불러오기", description = "채팅방 과거 메세지 불러오기, 무한스크롤을 위한 페이지네이션 구현")
    @Parameters({
            @Parameter(name = "roomId", description = "채팅방 아이디"),
            @Parameter(name = "limit", description = "한번에 불러올 메세지 개수"),
            @Parameter(name = "lastTime", description = "기준이 되는 시간, 이 시간을 기준으로 이전 채팅 메세지 불러오기", example = "2024-11-05T18:36:31.195451")
    })
    @GetMapping("/message")
    ResponseEntity<?> getMessageByRoomId(
            @AuthenticationPrincipal String principal,
            @RequestParam Integer roomId,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> lastTime);
}
