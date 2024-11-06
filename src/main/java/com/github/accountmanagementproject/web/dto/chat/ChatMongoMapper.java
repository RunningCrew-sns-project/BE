package com.github.accountmanagementproject.web.dto.chat;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ChatMongoMapper {
    ChatMongoMapper INSTANCE = Mappers.getMapper(ChatMongoMapper.class);

    @Mapping(target = "isSentByUser", expression = "java(chatDto.getSender().equals(currentUser))")
    ChatMongoDto chatDtoToChatMongoDto(ChatDto chatDto, String currentUser);
}
