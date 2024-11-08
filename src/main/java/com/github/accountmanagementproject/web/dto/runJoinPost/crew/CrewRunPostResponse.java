package com.github.accountmanagementproject.web.dto.runJoinPost.crew;

import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;
import com.github.accountmanagementproject.repository.runningPost.enums.CrewRunJoinPostStatus;
import com.github.accountmanagementproject.repository.runningPost.enums.PostType;
import com.github.accountmanagementproject.web.dto.storage.FileDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
public class CrewRunPostResponse {

    private Long runId;
    private Long crewId;             // 크루 ID
    private Long authorId;           // 작성자 ID
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



    public static CrewRunPostResponse toDto(CrewJoinPost runJoinPost) {

        // 이미지 정보를 FileDto로 변환
        List<FileDto> fileDtos = runJoinPost.getCrewJoinPostImages() != null ?
                runJoinPost.getCrewJoinPostImages().stream()
                        .map(image -> new FileDto(
                                image.getFileName(),
                                image.getImageUrl()
                        ))
                        .toList()
                : new ArrayList<>();

        // 참여 인원 수 계산
        int participantCount = runJoinPost.getParticipants() != null ? runJoinPost.getParticipants().size() : 0;

        return CrewRunPostResponse.builder()
                .runId(runJoinPost.getCrewPostId())
                .crewId(runJoinPost.getCrew() != null ? runJoinPost.getCrew().getCrewId() : null)
                .authorId(runJoinPost.getAuthor() != null ? runJoinPost.getAuthor().getUserId() : null)
                .title(runJoinPost.getTitle())
                .content(runJoinPost.getContent())
                .maximumPeople(runJoinPost.getMaximumPeople())
                .people(participantCount)  // 현재인원 추가
                .location(runJoinPost.getLocation())
                .status(runJoinPost.getStatus())
                .postType(runJoinPost.getPostType())
                .date(runJoinPost.getDate())  // 날짜 추가
                .startTime(runJoinPost.getStartTime() != null ? runJoinPost.getStartTime() : null)
                .inputLocation(runJoinPost.getInputLocation())
                .inputLatitude(runJoinPost.getInputLatitude())
                .inputLongitude(runJoinPost.getInputLongitude())
                .targetLocation(runJoinPost.getTargetLocation())
                .targetLatitude(runJoinPost.getTargetLatitude())
                .targetLongitude(runJoinPost.getTargetLongitude())
                .distance(runJoinPost.getDistance())
                .createdAt(runJoinPost.getCreatedAt())
                .updatedAt(runJoinPost.getUpdatedAt())
                .banners(fileDtos)
                .build();
    }
}
