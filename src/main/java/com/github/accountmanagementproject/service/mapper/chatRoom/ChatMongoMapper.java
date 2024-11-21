package com.github.accountmanagementproject.service.mapper.chatRoom;

import com.github.accountmanagementproject.repository.chat.ChatMongoDto;
import com.github.accountmanagementproject.web.dto.chat.ChatDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ChatMongoMapper {
    ChatMongoMapper INSTANCE = Mappers.getMapper(ChatMongoMapper.class);

    ChatMongoDto chatDtoToChatMongoDto(ChatDto chatDto, String currentUser);
}
