package com.github.accountmanagementproject.repository.chat;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_chat_mapping")
public class UserChatMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private MyUser user;

    @JoinColumn(name = "chatroom_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;
}
