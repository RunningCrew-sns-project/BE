package com.github.accountmanagementproject.web.dto.chat;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Document(collection = "ChatCollection")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMongoDto {
    @Id
    private String id;

    private String type;
    private Integer roomId;
    private String sender;
    private String message;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime time;
    private Boolean isSentByUser;

    @Override
    public String toString() {
        return "ChatMongoDto{" +
                "type='" + type + '\'' +
                ", roomId=" + roomId +
                ", sender='" + sender + '\'' +
                ", message='" + message + '\'' +
                ", time=" + time +
                '}';
    }
}
