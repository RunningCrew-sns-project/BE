package com.github.accountmanagementproject.repository.chat;

import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.web.dto.chat.ChatRoom;
import com.github.accountmanagementproject.web.dto.chat.UserChatMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserChatMappingRepository extends JpaRepository<UserChatMapping, Integer> {
    List<UserChatMapping> findAllByChatRoom(ChatRoom chatRoom);

    UserChatMapping findByChatRoom(ChatRoom chatRoom);

    void deleteByChatRoomAndUser(ChatRoom chatRoom, MyUser user);
}
