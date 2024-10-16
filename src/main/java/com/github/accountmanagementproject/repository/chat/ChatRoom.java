package com.github.accountmanagementproject.repository.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@Table(name = "chat_room")
@NoArgsConstructor
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "create_date")
    private Date newDate;

    public ChatRoom(String title, Date newDate) {
        this.title = title;
        this.newDate = newDate;
    }
}
