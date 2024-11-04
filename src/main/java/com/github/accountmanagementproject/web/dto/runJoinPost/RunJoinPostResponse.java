package com.github.accountmanagementproject.web.dto.runJoinPost;

import com.github.accountmanagementproject.repository.runningPost.RunJoinPost;
import com.github.accountmanagementproject.repository.runningPost.enums.PostType;
import com.github.accountmanagementproject.repository.runningPost.enums.RunJoinPostStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class RunJoinPostResponse {

    private Long postId;
    private Integer crewId;             // 크루 ID
    private Integer authorId;           // 작성자 ID
    private String title;
    private String content;
    private Integer maxParticipants;
    private RunJoinPostStatus status;   // 게시글 상태
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

    // 엔티티 -> DTO 변환
    public static RunJoinPostResponse toDto(RunJoinPost runJoinPost) {
        return RunJoinPostResponse.builder()
                .postId(runJoinPost.getPostId())
                .crewId(Math.toIntExact(runJoinPost.getCrew() != null ? runJoinPost.getCrew().getCrewId() : null))
                .authorId(Math.toIntExact(runJoinPost.getAuthor().getUserId()))
                .title(runJoinPost.getTitle())
                .content(runJoinPost.getContent())
                .maxParticipants(runJoinPost.getMaxParticipants())
                .status(runJoinPost.getStatus())
                .postType(runJoinPost.getPostType())
                .inputLocation(runJoinPost.getInputLocation())
                .inputLatitude(runJoinPost.getInputLatitude())
                .inputLongitude(runJoinPost.getInputLongitude())
                .targetLocation(runJoinPost.getTargetLocation())
                .targetLatitude(runJoinPost.getTargetLatitude())
                .targetLongitude(runJoinPost.getTargetLongitude())
                .distance(runJoinPost.getDistance())
                .createdAt(runJoinPost.getCreatedAt())
                .updatedAt(runJoinPost.getUpdatedAt())
                .build();
    }
}
