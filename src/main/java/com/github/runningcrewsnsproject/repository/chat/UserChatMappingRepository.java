package com.github.runningcrewsnsproject.repository.chat;

import com.github.runningcrewsnsproject.repository.account.user.MyUser;
import com.github.runningcrewsnsproject.web.dto.chat.ChatRoom;
import com.github.runningcrewsnsproject.web.dto.chat.UserChatMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserChatMappingRepository extends JpaRepository<UserChatMapping, Integer> {
    List<UserChatMapping> findAllByChatRoom(ChatRoom chatRoom);
    List<UserChatMapping> findAllByUser(MyUser user);

    UserChatMapping findByUserAndChatRoom(MyUser user, ChatRoom chatRoom);

    void deleteByChatRoomAndUser(ChatRoom chatRoom, MyUser user);
}
