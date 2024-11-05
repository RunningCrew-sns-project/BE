package com.github.runningcrewsnsproject.web.dto.chat;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ChatMongoMapper {
    ChatMongoMapper INSTANCE = Mappers.getMapper(ChatMongoMapper.class);

    ChatMongoDto chatDtoToChatMongoDto(ChatDto chatDto);
}
