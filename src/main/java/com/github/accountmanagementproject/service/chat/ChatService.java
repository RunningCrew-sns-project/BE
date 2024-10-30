package com.github.accountmanagementproject.service.chat;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.repository.chat.ChatRoomRepository;
import com.github.accountmanagementproject.repository.chat.UserChatMappingRepository;
import com.github.accountmanagementproject.service.mapper.chatRoom.ChatRoomMapper;
import com.github.accountmanagementproject.service.mapper.user.UserResponseMapper;
import com.github.accountmanagementproject.web.dto.chat.ChatRoom;
import com.github.accountmanagementproject.web.dto.chat.ChatRoomResponse;
import com.github.accountmanagementproject.web.dto.chat.UserChatMapping;
import com.github.accountmanagementproject.web.dto.chat.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final MyUsersRepository myUsersJpa;
    private final AccountConfig accountConfig;
    private final UserChatMappingRepository userChatMappingRepository;

    // 전체 채팅방 조회
    public List<ChatRoomResponse> findAllRoom(){
        //채팅방 생성 순서를 최근순으로 반환
        return chatRoomRepository.findAll().stream()
                .map(chatRoom -> {
                    List<UserChatMapping> userChatMappingList = userChatMappingRepository.findAllByChatRoom(chatRoom);
                    List<MyUser> userList = userChatMappingList.stream().map(UserChatMapping::getUser).toList();
                    List<UserResponse> userResponses = userList.stream().map(UserResponseMapper.INSTANCE::myUserToUserResponse).toList();
                    ChatRoomResponse chatRoomResponse = ChatRoomMapper.INSTANCE.chatRoomToChatRoomResponse(chatRoom);
                    chatRoomResponse.setUserCount(userList.size());
                    chatRoomResponse.setUserList(userResponses);
                    return chatRoomResponse;
                })
                .toList();
    }

    // roomId 기준으로 채팅방 찾기
    public ChatRoom findByRoomId(Integer roomId){
        return chatRoomRepository.findById(roomId).orElse(null);
    }

    public List<ChatRoomResponse> findMyRoomList(MyUser user) {
        return userChatMappingRepository.findAllByUser(user).stream()
                .map(UserChatMapping::getChatRoom)
                .map(ChatRoomMapper.INSTANCE::chatRoomToChatRoomResponse)
                .toList();
    }

    @Transactional
    // roomName 으로 채팅방 만들기
    public ChatRoom createChatRoom(String roomName, MyUser user){
        //채팅방 이름으로 채팅 방 생성후
        ChatRoom chatRoom = ChatRoom.builder()
                .title(roomName)
                .createdAt(LocalDateTime.now())
                .build();
        chatRoomRepository.save(chatRoom);

        //Repository에 채팅방 저장
        UserChatMapping userChatMapping = UserChatMapping.builder()
                .user(user)
                .chatRoom(chatRoom)
                .build();
        userChatMappingRepository.save(userChatMapping);
        List<MyUser> userList = userChatMappingRepository.findAllByChatRoom(chatRoom).stream().map(UserChatMapping::getUser).toList();
        chatRoom.setUserCount(userList.size());
        return chatRoom;
    }
//
//    @Transactional
//    // 채팅방 인원 +1
//    public void increaseUser(Integer roomId){
//        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
//        chatRoom.setUserCount(chatRoom.getUserCount() + 1);
//        chatRoomRepository.save(chatRoom);
//    }
//
//    @Transactional
//    // 채팅방 인원 -1
//    public void decreaseUser(Integer roomId){
//        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
//        chatRoom.setUserCount(chatRoom.getUserCount()-1);
//        chatRoomRepository.save(chatRoom);
//    }

    @Transactional
    //채팅방 유저 리스트에 유저추가
    public Boolean addUser(Integer roomId, MyUser user){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
        //아이디 중복 확인 후 userList에 추가
        List<MyUser> userList = userChatMappingRepository.findAllByChatRoom(chatRoom).stream().map(UserChatMapping::getUser).toList();

        if(userList.contains(user)){
            return true;
        }

        UserChatMapping userChatMapping = UserChatMapping.builder()
                        .chatRoom(chatRoom)
                        .user(user)
                        .build();

        userChatMappingRepository.save(userChatMapping);

        Objects.requireNonNull(chatRoom).setUserCount(userList.size());

        chatRoomRepository.save(chatRoom);

        return false;
    }

    @Transactional
    // 채팅방 유저 리스트 삭제
    public void deleteUser(Integer roomId, MyUser user){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
//        this.decreaseUser(roomId);
        UserChatMapping userChatMapping = userChatMappingRepository.findByUserAndChatRoom(user, chatRoom);
        userChatMappingRepository.delete(userChatMapping);

        Objects.requireNonNull(chatRoom).setUserCount(chatRoom.getUserCount() - 1);

        userChatMappingRepository.flush();
    }

    //채팅방 전체 userList 조회
    public List<String> getUserList(Integer roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);

        return userChatMappingRepository.findAllByChatRoom(chatRoom).stream()
                .map(UserChatMapping::getUser)
                .map(MyUser::getEmail)
                .toList();
    }


}
