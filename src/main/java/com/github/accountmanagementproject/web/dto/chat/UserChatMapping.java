package com.github.accountmanagementproject.web.dto.chat;

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

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private MyUser user;

    @JoinColumn(name = "chatroom_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;
}
