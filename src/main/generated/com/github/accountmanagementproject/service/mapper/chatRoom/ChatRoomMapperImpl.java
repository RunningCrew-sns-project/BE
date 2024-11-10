package com.github.accountmanagementproject.service.mapper.chatRoom;

import com.github.accountmanagementproject.web.dto.chat.ChatRoom;
import com.github.accountmanagementproject.web.dto.chat.ChatRoomResponse;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-10T18:03:13+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (JetBrains s.r.o.)"
)
public class ChatRoomMapperImpl implements ChatRoomMapper {

    @Override
    public ChatRoomResponse chatRoomToChatRoomResponse(ChatRoom chatRoom) {
        if ( chatRoom == null ) {
            return null;
        }

        ChatRoomResponse.ChatRoomResponseBuilder chatRoomResponse = ChatRoomResponse.builder();

        chatRoomResponse.roomId( chatRoom.getRoomId() );
        chatRoomResponse.chatRoomImage( chatRoom.getChatRoomImage() );
        chatRoomResponse.title( chatRoom.getTitle() );
        chatRoomResponse.userCount( chatRoom.getUserCount() );

        return chatRoomResponse.build();
    }
}
