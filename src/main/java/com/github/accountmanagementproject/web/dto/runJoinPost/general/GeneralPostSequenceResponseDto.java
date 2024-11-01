package com.github.accountmanagementproject.web.dto.runJoinPost.general;

import com.github.accountmanagementproject.repository.runningPost.RunJoinPost;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostResponse;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;


@Data
public class GeneralPostSequenceResponseDto {

    private Integer generalPostSequence;
    private List<GeneralRunPostResponse> postDetails;

    public GeneralPostSequenceResponseDto(List<RunJoinPost> posts) {
        if (!posts.isEmpty()) {
            this.generalPostSequence = posts.get(0).getGeneralPostSequence();  // 리스트 내의 모든 항목이 동일한 generalPostSequence를 가정
            this.postDetails = posts.stream()
                    .map(post -> GeneralRunPostResponse.builder()
                            .postId(post.getPostId())
                            .generalPostSequence(post.getGeneralPostSequence())
                            .crewId(post.getCrew() != null ? Math.toIntExact(post.getCrew().getCrewId()) : null)  // crewId가 null일 경우 처리
                            .authorId(post.getAuthor() != null && post.getAuthor().getUserId() != null
                                    ? Math.toIntExact(post.getAuthor().getUserId())
                                    : null)  // authorId가 null일 경우 처리
                            .title(post.getTitle())
                            .content(post.getContent())
                            .maxParticipants(post.getMaxParticipants())
                            .status(post.getStatus())
                            .postType(post.getPostType())
                            .inputLocation(post.getInputLocation())
                            .inputLatitude(post.getInputLatitude())
                            .inputLongitude(post.getInputLongitude())
                            .targetLocation(post.getTargetLocation())
                            .targetLatitude(post.getTargetLatitude())
                            .targetLongitude(post.getTargetLongitude())
                            .distance(post.getDistance())
                            .createdAt(post.getCreatedAt())
                            .updatedAt(post.getUpdatedAt())
                            .build())
                    .collect(Collectors.toList());
        }
    }
}
