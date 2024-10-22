package com.github.accountmanagementproject.web.dto.chat;

import com.github.accountmanagementproject.repository.account.users.MyUser;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    private List<UserResponse> userList;
}
