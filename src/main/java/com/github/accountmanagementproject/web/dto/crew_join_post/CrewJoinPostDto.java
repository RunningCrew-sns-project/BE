package com.github.accountmanagementproject.web.dto.crew_join_post;

import com.github.accountmanagementproject.repository.crew_join_post.CrewJoinPost;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Builder
@Getter
public class CrewJoinPostDto {

    private Integer crewJoinPostId;
    private Integer crewId;
    private Integer userId;

    private String content;
    private Integer maxCrewNumber;
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String inputAddress;
    private double inputLatitude;
    private double inputLongitude;

    private String targetAddress;
    private double targetLatitude;
    private double targetLongitude;

    private double distance;


    public static CrewJoinPostDto toDto(CrewJoinPost crewJoinPost) {
        return CrewJoinPostDto.builder()
                .crewJoinPostId(crewJoinPost.getId())
                .crewId(crewJoinPost.getCrew() != null ? crewJoinPost.getCrew().getId() : null)
                .userId(crewJoinPost.getUser() != null ? crewJoinPost.getUser().getUserId() : null)
                .content(crewJoinPost.getContent())
                .maxCrewNumber(crewJoinPost.getMaxCrewNumber())
                .status(crewJoinPost.getStatus().name())  // Enum 타입을 String으로 변환
                .createdAt(crewJoinPost.getCreatedAt())
                .updatedAt(crewJoinPost.getUpdatedAt())
                .inputAddress(crewJoinPost.getInputAddress())
                .inputLatitude(crewJoinPost.getInputLatitude())
                .inputLongitude(crewJoinPost.getInputLongitude())
                .targetAddress(crewJoinPost.getTargetAddress())
                .targetLatitude(crewJoinPost.getTargetLatitude())
                .targetLongitude(crewJoinPost.getTargetLongitude())
                .distance(crewJoinPost.getDistance())
                .build();
    }
}
