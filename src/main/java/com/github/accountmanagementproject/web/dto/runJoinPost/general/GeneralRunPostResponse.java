package com.github.accountmanagementproject.web.dto.runJoinPost.general;

import com.github.accountmanagementproject.repository.runningPost.RunJoinPost;
import com.github.accountmanagementproject.repository.runningPost.enums.PostType;
import com.github.accountmanagementproject.repository.runningPost.enums.RunJoinPostStatus;
import com.github.accountmanagementproject.web.dto.storage.FileDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
public class GeneralRunPostResponse {

    private Long postId;
    private Integer generalPostSequence;
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

    private List<FileDto> fileDtos;  // 파일 이미지


    public static GeneralRunPostResponse toDto(RunJoinPost runJoinPost) {

        // 이미지 정보를 FileDto로 변환
        List<FileDto> fileDtos = runJoinPost.getJoinPostImages() != null ?
                runJoinPost.getJoinPostImages().stream()
                        .map(image -> new FileDto(
                                image.getFileName(),
                                image.getImageUrl()
                        ))
                        .toList()
                : new ArrayList<>();

        return GeneralRunPostResponse.builder()
                .postId(runJoinPost.getPostId())
                .generalPostSequence(runJoinPost.getGeneralPostSequence())
                .crewId(runJoinPost.getCrew() != null ? Math.toIntExact(runJoinPost.getCrew().getCrewId()) : null)
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
                .fileDtos(fileDtos)  // 변환된 이미지 정보 추가
                .build();
    }
}