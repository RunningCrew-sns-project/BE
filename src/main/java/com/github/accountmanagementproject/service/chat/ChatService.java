package com.github.accountmanagementproject.service.chat;

import com.github.accountmanagementproject.exception.CustomBadCredentialsException;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.chat.ChatMongoRepository;
import com.github.accountmanagementproject.repository.chat.ChatRoomRepository;
import com.github.accountmanagementproject.repository.chat.UserChatMappingRepository;
import com.github.accountmanagementproject.service.ExeTimer;
import com.github.accountmanagementproject.service.ScrollPaginationCollection;
import com.github.accountmanagementproject.service.mapper.chatRoom.ChatRoomMapper;
import com.github.accountmanagementproject.web.dto.chat.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService{
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatMappingRepository userChatMappingRepository;
    private final ChatMongoRepository chatMongoRepository;
    private final MongoTemplate mongoTemplate;

    @ExeTimer
    public ScrollPaginationCollection<ChatRoomResponse> findAllRoom(Integer size, Integer cursor) {
        Integer lastRoomId = (cursor != null) ? cursor : chatRoomRepository.findTopByOrderByRoomIdDesc().getRoomId();
        PageRequest pageRequest = PageRequest.of(0, size + 1);

        Page<ChatRoom> chatRoomPage = chatRoomRepository.findByRoomIdLessThanOrderByRoomIdDesc(lastRoomId + 1,pageRequest);

        List<ChatRoom> chatRoomList = chatRoomPage.getContent();

        List<ChatRoomResponse> responseList = chatRoomList.stream().map(this::mappingChatRoom).toList();

        boolean lastScroll = responseList.size() <= size;
        List<ChatRoomResponse> currentScrollItems = lastScroll ? responseList : responseList.subList(0, size);

        ChatRoomResponse nextCursor = lastScroll ? null : responseList.get(size);

        return ScrollPaginationCollection.of(currentScrollItems, size, lastScroll, nextCursor);
    }

    @ExeTimer
    public List<ChatRoomResponse> findMyRoomList(MyUser user) {
        return userChatMappingRepository.findAllByUser(user)
                .stream()
                .map(UserChatMapping::getChatRoom)
                .map(this::mappingChatRoom)
                .toList();
    }

    private ChatRoomResponse mappingChatRoom(ChatRoom chatRoom){
        List<UserChatMapping> userChatMappingList = userChatMappingRepository.findAllByChatRoom(chatRoom);
        ChatRoomResponse chatRoomResponse = ChatRoomMapper.INSTANCE.chatRoomToChatRoomResponse(chatRoom);
        chatRoomResponse.setUserCount(userChatMappingList.size());

        if(!chatMongoRepository.findAllByRoomId(chatRoom.getRoomId()).isEmpty()) {
            chatRoomResponse.setLastMessage(chatMongoRepository.findFirstByRoomIdOrderByTimeDesc(chatRoom.getRoomId()).getMessage());
            chatRoomResponse.setLastMessageTime(chatMongoRepository.findFirstByRoomIdOrderByTimeDesc(chatRoom.getRoomId()).getTime());
        }

        return chatRoomResponse;
    }

    @ExeTimer
    @Transactional
    // roomName 으로 채팅방 만들기
    public ChatRoom createChatRoom(String roomName, MyUser user){
        //채팅방 이름으로 채팅 방 생성후
        if(chatRoomRepository.findByTitle(roomName) == null) {
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
        }else {
            return chatRoomRepository.findByTitle(roomName);
        }
    }

    @ExeTimer
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

    @ExeTimer
    @Transactional
    // 채팅방 유저 리스트 삭제
    public void deleteUser(Integer roomId, MyUser user){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
        UserChatMapping userChatMapping = userChatMappingRepository.findByUserAndChatRoom(user, chatRoom);
        userChatMappingRepository.delete(userChatMapping);

        Objects.requireNonNull(chatRoom).setUserCount(chatRoom.getUserCount() - 1);

        userChatMappingRepository.flush();

        //모든 유저가 나가면 채팅방 삭제
        if(chatRoom.getUserCount() == 0){
            chatRoomRepository.delete(chatRoom);
        }
    }

    @ExeTimer
    //채팅방 전체 userList 조회
    public List<UserResponse> getUserList(Integer roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);

        return userChatMappingRepository.findAllByChatRoom(chatRoom)
                .stream()
                .map(userChatMapping -> {
                    MyUser user = userChatMapping.getUser();
                    return UserResponse.builder()
                            .userId(user.getUserId())
                            .userName(user.getNickname())
                            .build();
                }).toList();
    }

    @ExeTimer
    public List<ChatMongoDto> getMessageByRoomId(Integer roomId, MyUser user, Integer limit, Optional<LocalDateTime> lastTime) {
        LocalDateTime lastTimeStamp = lastTime
                .orElse(LocalDateTime.now())
                .atZone(ZoneId.systemDefault())  // 현재 시스템 시간대를 사용해 ZonedDateTime 생성
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"))  // 한국 시간대로 변환
                .toLocalDateTime();

        log.info(lastTimeStamp.toString());

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
        if(!userChatMappingRepository.findAllByChatRoom(chatRoom).stream().map(UserChatMapping::getUser).toList().contains(user)){
            throw  new CustomBadCredentialsException.ExceptionBuilder()
                    .customMessage("참여한 유저가 아닙니다.")
                    .build();
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId)
                .and("time").lt(lastTimeStamp));
        query.with(Sort.by(Sort.Direction.DESC, "time"));
        query.limit(limit);

        log.info(chatMongoRepository.findAllByRoomId(roomId).toString());
//        log.info(String.valueOf(mongoTemplate.find(query, ChatMongoDto.class).get(0).toString()));
        return mongoTemplate.find(query, ChatMongoDto.class);
    }



}
