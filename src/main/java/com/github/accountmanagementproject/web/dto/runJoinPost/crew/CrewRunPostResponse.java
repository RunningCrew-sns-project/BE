package com.github.accountmanagementproject.web.dto.runJoinPost.crew;

import com.github.accountmanagementproject.repository.runningPost.enums.CrewRunJoinPostStatus;
import com.github.accountmanagementproject.repository.runningPost.enums.PostType;
import com.github.accountmanagementproject.web.dto.storage.FileDto;
import com.github.accountmanagementproject.web.dto.storage.UrlDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Data
@Builder
public class CrewRunPostResponse {

    private Long runId;
    private Long crewId;             // 크루 ID
    private Long authorId;           // 작성자 ID
    // 크루 info
    private String crewName;
    private String crewDescription;
    private String crewImageUrl;

    private String title;
    private String content;
    private Integer maximumPeople;      // 최대인원
    private Integer people;             // 현재인원
    private String location;

    private LocalDate date;             // 모임 날짜
    private LocalTime startTime;        // 모임 시작 시간, 없으면 null

    private CrewRunJoinPostStatus status;   // 게시글 상태
    private PostType postType;          // 게시자 분류

    // 시작 위치 정보
    private String inputLocation;
    private double inputLatitude;
    private double inputLongitude;

    // 종료 위치 정보
    private String targetLocation;
    private double targetLatitude;
    private double targetLongitude;

    private double distance;             // 총 거리
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<FileDto> banners;  // 파일 이미지



}
