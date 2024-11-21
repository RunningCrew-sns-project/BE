package com.github.accountmanagementproject.repository.chat;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface ChatMongoRepository extends MongoRepository<ChatMongoDto, String> {
    List<ChatMongoDto> findAllByRoomId(Integer roomId);
//    ChatMongoDto findByRoomId(Integer roomId);
    ChatMongoDto findFirstByRoomIdOrderByTimeDesc(Integer roomId);

}
