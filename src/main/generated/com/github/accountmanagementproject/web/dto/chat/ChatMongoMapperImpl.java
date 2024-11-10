package com.github.accountmanagementproject.web.dto.chat;

import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-10T18:03:13+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (JetBrains s.r.o.)"
)
public class ChatMongoMapperImpl implements ChatMongoMapper {

    @Override
    public ChatMongoDto chatDtoToChatMongoDto(ChatDto chatDto, String currentUser) {
        if ( chatDto == null && currentUser == null ) {
            return null;
        }

        ChatMongoDto.ChatMongoDtoBuilder chatMongoDto = ChatMongoDto.builder();

        if ( chatDto != null ) {
            if ( chatDto.getType() != null ) {
                chatMongoDto.type( chatDto.getType().name() );
            }
            chatMongoDto.roomId( chatDto.getRoomId() );
            chatMongoDto.sender( chatDto.getSender() );
            chatMongoDto.message( chatDto.getMessage() );
            chatMongoDto.time( chatDto.getTime() );
        }
        chatMongoDto.isSentByUser( chatDto.getSender().equals(currentUser) );

        return chatMongoDto.build();
    }
}
