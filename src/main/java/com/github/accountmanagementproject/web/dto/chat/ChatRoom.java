package com.github.accountmanagementproject.web.dto.chat;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_room")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Integer roomId;  // 채팅방 아이디

    @Column(name = "title")
    private String title;// 채팅방 이름

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder.Default
    @Column(name = "user_count")
    private Integer userCount = 0;

    @Builder.Default
    @Column(name = "chat_room_image")
    private String chatRoomImage = "https://running-crew.s3.ap-northeast-2.amazonaws.com/default_image/chat_room_default.jpg";

    @Override
    public String toString() {
        return "ChatRoom{" +
                "roomId='" + roomId + '\'' +
                ", title='" + title + '\'' +
                ", createdAt=" + createdAt +
                ", userCount=" + userCount +
                '}';
    }
}
