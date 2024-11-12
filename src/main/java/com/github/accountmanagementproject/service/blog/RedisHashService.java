package com.github.accountmanagementproject.service.blog;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class RedisHashService {
    private final RedisTemplate<String, String> redisTemplate;
    private HashOperations<String, String, String> hashOperations;

    //https://velog.io/@bagt/Redis-%EC%97%AD%EC%A7%81%EB%A0%AC%ED%99%94-%EC%82%BD%EC%A7%88%EA%B8%B0-feat.-RedisSerializer
    //redis의 값 직렬화, 역직렬화 관련
    @Autowired
    public RedisHashService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    //키, 필드, 밸류 조합으로 레디스 저장
    //
    public void save(String key, String field, String value) {
        hashOperations.put(key, field, value);
    }

    //키, 필드 값으로 밸류 조회
    public String get(String key, String field) {
        return hashOperations.get(key, field);
    }

    //키의 모든 필드, 밸류 쌍 조회
    public Map<String, String> getAll(String key){
        return hashOperations.entries(key);
    }

    public Boolean exists(String key){
       return redisTemplate.hasKey(key);
    }

    public List<String> multiGet(String key, List<String> field) {
        return hashOperations.multiGet(key, field).stream()
                .map(value-> value == null ? "0" : "1")
                .toList();
    }

    public void delete(String key, String field) {
        hashOperations.delete(key, field);
    }

    public void increment(String key, String field) {
        hashOperations.increment(key, field, 1);
    }

    public void decrement(String key, String field) {
        hashOperations.increment(key, field, -1);
    }

}
