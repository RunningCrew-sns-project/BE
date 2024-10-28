package com.github.accountmanagementproject.web.dto.crew_join_post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;


@Getter
@Setter
@AllArgsConstructor
public class CrewJoinPostRequest {

    // 추가 필드
    private String crewName;       // 크루 이름
    private String content;        // 게시글 내용


    private Integer maxCrewNumber; // 최대 인원
//    private String image;          // 이미지 URL
}
