package com.github.accountmanagementproject.repository.chat;

import com.github.accountmanagementproject.web.dto.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//참고 : https://velog.io/@jyyoun1022/Spring-Web-Socket%EC%9B%B9-%EC%86%8C%EC%BC%93%EA%B3%BC-Chatting%EC%B1%84%ED%8C%85-3
@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

}
