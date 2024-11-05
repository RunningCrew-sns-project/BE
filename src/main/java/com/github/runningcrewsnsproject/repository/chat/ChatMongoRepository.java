package com.github.runningcrewsnsproject.repository.chat;

import com.github.runningcrewsnsproject.web.dto.chat.ChatMongoDto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface ChatMongoRepository extends MongoRepository<ChatMongoDto, String> {
    List<ChatMongoDto> findAllByRoomId(Integer roomId);

}
