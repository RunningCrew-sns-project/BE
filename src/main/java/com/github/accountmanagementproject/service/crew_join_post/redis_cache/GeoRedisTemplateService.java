package com.github.accountmanagementproject.service.crew_join_post.redis_cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.accountmanagementproject.web.dto.crew_join_post.CrewJoinPostDto;
import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class GeoRedisTemplateService {

    private static final String CACHE_KEY = "LOCATION";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private HashOperations<String, String, String> hashOperations;

    @PostConstruct
    public void init() {
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void save(CrewJoinPostDto crewJoinPostDto) {
        if(Objects.isNull(crewJoinPostDto) || Objects.isNull(crewJoinPostDto.getCrewJoinPostId())) {
            log.error("Required Values must not be null");
            return;
        }

        try {
            hashOperations.put(CACHE_KEY,
                    crewJoinPostDto.getCrewJoinPostId().toString(),
                    serializeCrewJoinPostDtoDto(crewJoinPostDto));
            log.info("[GeoRedisTemplateService save success] id: {}", crewJoinPostDto.getCrewJoinPostId());
        } catch (Exception e) {
            log.error("[GeoRedisTemplateService save error] {}", e.getMessage());
        }
    }

    public List<CrewJoinPostDto> findAll() {

        try {
            List<CrewJoinPostDto> list = new ArrayList<>();
            for (String value : hashOperations.entries(CACHE_KEY).values()) {
                CrewJoinPostDto crewJoinPostDto = deserializeCrewJoinPostDto(value);
                list.add(crewJoinPostDto);
            }
            return list;

        } catch (Exception e) {
            log.error("[GeoRedisTemplateService findAll error]: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<CrewJoinPostDto> findFiltered(PageRequestDto pageRequestDto) {
        String location = pageRequestDto.getLocation();
        int pageSize = pageRequestDto.getSize();
        int offset = (pageRequestDto.getPage() - 1) * pageSize;

        return redisTemplate.opsForHash().values(CACHE_KEY).stream()
                .map(value -> {
                            try {
                                return deserializeCrewJoinPostDto((String) value);
                            } catch (JsonProcessingException e) {
                                log.error("Failed to deserialize value: {}", value, e);
                                return null; // 문제가 있는 경우 null을 반환
                            }
                        })
                .filter(Objects::nonNull) // null 값 제거
                .filter(dto -> location.equals("전체") || dto.getInputAddress().contains(location))
                .skip(offset)
                .limit(pageSize)
                .collect(Collectors.toList());
    }




    public CrewJoinPostDto findById(Integer crewJoinPostId) {
        try {
            // Redis에서 데이터 조회
            String data = (String) redisTemplate.opsForHash().get(CACHE_KEY, crewJoinPostId.toString());
            if (data != null) {
                // JSON 문자열을 CrewJoinPostDto 객체로 역직렬화
                return objectMapper.readValue(data, CrewJoinPostDto.class);
            }
        } catch (JsonProcessingException e) {
            log.error("[GeoRedisTemplateService findById error]: {}", e.getMessage());
        }
        return null; // 데이터가 없거나 오류 발생 시 null 반환
    }

    public void delete(Long id) {
        hashOperations.delete(CACHE_KEY, String.valueOf(id));
        log.info("[GeoRedisTemplateService delete]: {} ", id);
    }

    private String serializeCrewJoinPostDtoDto(CrewJoinPostDto crewJoinPostDto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(crewJoinPostDto);
    }

    private CrewJoinPostDto deserializeCrewJoinPostDto(String value) throws JsonProcessingException {
        return objectMapper.readValue(value, CrewJoinPostDto.class);
    }
}
