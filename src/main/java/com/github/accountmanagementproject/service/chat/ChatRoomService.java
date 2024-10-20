package com.github.accountmanagementproject.service.chat;

import com.github.accountmanagementproject.repository.chat.ChatRoom;
import com.github.accountmanagementproject.repository.chat.ChatRoomRepository;
import com.github.accountmanagementproject.web.dto.chat.RequestChatRoomDto;
import com.github.accountmanagementproject.web.dto.chat.ResponseChatRoomDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ResponseChatRoomDto createChatRoom(RequestChatRoomDto requestChatRoomDto) {
        ChatRoom chatRoom = new ChatRoom(requestChatRoomDto.getTitle(), new Date());
        return ResponseChatRoomDto.of(chatRoomRepository.save(chatRoom));
    }

    @Transactional
    public List<ResponseChatRoomDto> findChatRoomList() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        return chatRooms.stream().map(ResponseChatRoomDto::of).collect(Collectors.toList());
    }
}


