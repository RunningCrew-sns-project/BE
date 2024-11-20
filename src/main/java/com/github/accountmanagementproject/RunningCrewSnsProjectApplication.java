package com.github.accountmanagementproject;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@RequiredArgsConstructor
public class RunningCrewSnsProjectApplication {
    private final RedisTemplate<String, String> redisTemplate;
    public static void main(String[] args) {
        SpringApplication.run(RunningCrewSnsProjectApplication.class, args);
    }

}
