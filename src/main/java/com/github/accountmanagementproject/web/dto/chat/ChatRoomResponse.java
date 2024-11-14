package com.github.accountmanagementproject.web.dto.chat;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResponse {
    private Integer roomId;

    private String chatRoomImage;

    private String title;

    private String lastMessage;

    private Integer userCount;

    private LocalDateTime lastMessageTime;
}
