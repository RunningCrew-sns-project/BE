package com.github.accountmanagementproject.repository.redis;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Set;


@Repository
public class RedisRepository {

    private final ValueOperations<String, String> valueOperations;
    public RedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.valueOperations = redisTemplate.opsForValue();
    }


    public void save(String key, String value, Duration exp){
        valueOperations.set(key, value, exp);
    }

    public String getValue(String key){
        return valueOperations.get(key);
    }

    public Set<String> keys(String pattern){return valueOperations.getOperations().keys(pattern);}
    public String getAndDeleteValue(String key){
        return valueOperations.getAndDelete(key);
    }


}