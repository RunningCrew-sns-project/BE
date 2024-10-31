package com.github.accountmanagementproject.service.mapper.chatRoom;

import com.github.accountmanagementproject.web.dto.chat.ChatRoom;
import com.github.accountmanagementproject.web.dto.chat.ChatRoomResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ChatRoomMapper {
    ChatRoomMapper INSTANCE = Mappers.getMapper(ChatRoomMapper.class);

    ChatRoomResponse chatRoomToChatRoomResponse(ChatRoom chatRoom);
}
