package com.github.accountmanagementproject.service.mappers.chatRoom;

import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.chat.UserChatMappingRepository;
import com.github.accountmanagementproject.web.dto.chat.ChatRoom;
import com.github.accountmanagementproject.web.dto.chat.ChatRoomResponse;
import com.github.accountmanagementproject.web.dto.chat.UserChatMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ChatRoomMapper {
    ChatRoomMapper INSTANCE = Mappers.getMapper(ChatRoomMapper.class);

    ChatRoomResponse chatRoomToChatRoomResponse(ChatRoom chatRoom);
}
