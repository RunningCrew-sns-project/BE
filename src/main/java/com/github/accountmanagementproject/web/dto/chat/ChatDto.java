package com.github.accountmanagementproject.web.dto.chat;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChatDto {
    // 메시지 타입 :  입장 채팅
    // 메시지 타입에 따라서 동작하는 구조가 달라진다.
    // 입장과 퇴장 ENTER 과 LEAVE 의 경우 입장/퇴장 이벤트 처리가 실행되고,
    // TALK 는 말 그대로 해당 채팅방을 sub 하고 있는 모든 클라이언트에게 전달됩니다.
    public enum MessageType{
        ENTER, TALK, LEAVE
    }
    private MessageType type; //메시지 타입
    private Integer roomId;// 방 번호
    private String sender;//채팅을 보낸 사람 (id)
    private String userName; //유저 이름
    private String message;// 메세지
    private Boolean isSentByUser;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime time; // 채팅 발송 시간


}
