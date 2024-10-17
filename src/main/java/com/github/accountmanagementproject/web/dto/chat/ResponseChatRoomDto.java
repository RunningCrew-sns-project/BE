package com.github.accountmanagementproject.web.dto.chat;

import com.github.accountmanagementproject.repository.chat.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class ResponseChatRoomDto {
    private Integer id;
    private String title;
    private Date createDate;

    public static ResponseChatRoomDto of(ChatRoom chatRoom) {
        return new ResponseChatRoomDto(chatRoom.getId(), chatRoom.getTitle(), chatRoom.getNewDate());
    }
}
