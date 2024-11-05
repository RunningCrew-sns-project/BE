package com.github.runningcrewsnsproject.web.dto.runJoinPost.general;

import com.github.runningcrewsnsproject.repository.runningPost.RunJoinPost;
import com.github.runningcrewsnsproject.web.dto.storage.FileDto;
import lombok.Data;

import java.util.ArrayList;
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
                            .crewId(post.getCrew() != null ? Math.toIntExact(post.getCrew().getCrewId()) : null)
                            .authorId(Math.toIntExact(post.getAuthor().getUserId()))
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
                            // 이미지 정보 추가
                            .fileDtos(post.getJoinPostImages() != null ?
                                    post.getJoinPostImages().stream()
                                            .map(image -> new FileDto(
                                                    image.getFileName(),
                                                    image.getImageUrl()
                                            ))
                                            .collect(Collectors.toList())
                                    : new ArrayList<>())
                            .build())
                    .collect(Collectors.toList());
        }
    }
}
