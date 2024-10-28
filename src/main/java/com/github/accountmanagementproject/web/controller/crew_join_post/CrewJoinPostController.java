package com.github.accountmanagementproject.web.controller.crew_join_post;


import com.github.accountmanagementproject.repository.crew_join_post.CrewJoinPost;
import com.github.accountmanagementproject.repository.crew_join_post.repository.CrewJoinPostRepository;
import com.github.accountmanagementproject.service.crew_join_post.CrewJoinPostService;
import com.github.accountmanagementproject.service.crew_join_post.redis_cache.GeoRedisTemplateService;
import com.github.accountmanagementproject.web.dto.crew_join_post.CrewJoinPOstRequest;
import com.github.accountmanagementproject.web.dto.crew_join_post.CrewJoinPostDto;
import com.github.accountmanagementproject.web.dto.crew_join_post.CrewJoinPostUpdateRequest;
import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@RestController
public class CrewJoinPostController {

    private final CrewJoinPostRepository crewJoinPostRepository;
    private final GeoRedisTemplateService geoRedisTemplateService;
    private final CrewJoinPostService crewJoinPostService;



    // "크루 달리기 모집" 목록 가져오기
    @GetMapping("/list")
    public ResponseEntity<?> getAll(PageRequestDto pageRequestDto) {

        PageResponseDto<CrewJoinPostDto> response = crewJoinPostService.getAll(pageRequestDto);
        return ResponseEntity.ok(response);
    }


    // "크루 달리기 모집" 글 Post 하기
    @PostMapping("/save/{userId}")
    public ResponseEntity<?> save(@RequestBody CrewJoinPOstRequest inputDto, @PathVariable Integer userId) {
        CrewJoinPost savedPost = crewJoinPostService.save(inputDto, userId);
        CrewJoinPostDto responseDto = CrewJoinPostDto.toDto(savedPost);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // "크루 달리기 모집" 글 update 하기
    @PostMapping("/update/{userId}/{crewJoinPostId}")
    public ResponseEntity<CrewJoinPostDto> updateCrewJoinPost(
            @PathVariable Integer userId,
            @PathVariable Integer crewJoinPostId,
            @RequestBody CrewJoinPostUpdateRequest updateRequest) {

        CrewJoinPost updatedPost = crewJoinPostService.updateCrewJoinPost(updateRequest, userId, crewJoinPostId);

        // 엔티티를 DTO로 변환하여 반환
        CrewJoinPostDto responseDto = CrewJoinPostDto.toDto(updatedPost);
        return ResponseEntity.ok(responseDto);
    }


    // 상세보기
    @GetMapping("/{crewJoinPostId}")
    public ResponseEntity<CrewJoinPostDto> getDetail(@PathVariable Integer crewJoinPostId) {
        CrewJoinPostDto postDto = crewJoinPostService.getOneById(crewJoinPostId);
        return ResponseEntity.ok(postDto);
    }


    // 삭제
    @DeleteMapping("/{crewJoinPostId}")
    public ResponseEntity<?> deleteCrewJoinPost(@PathVariable Integer crewJoinPostId, @RequestParam Integer userId) {
        crewJoinPostService.deleteCrewJoinPost(crewJoinPostId, userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "CrewJoinPost has been successfully deleted.");
        return ResponseEntity.ok().body(response);
    }



}
