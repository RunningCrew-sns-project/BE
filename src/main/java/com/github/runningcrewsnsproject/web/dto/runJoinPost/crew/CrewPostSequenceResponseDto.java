package com.github.runningcrewsnsproject.web.dto.runJoinPost.crew;

import com.github.runningcrewsnsproject.repository.runningPost.RunJoinPost;
import com.github.runningcrewsnsproject.web.dto.storage.FileDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Data
public class CrewPostSequenceResponseDto {

    private Integer crewPostSequence;
    private List<CrewRunPostResponse> postDetails;

    public CrewPostSequenceResponseDto(List<RunJoinPost> posts) {
        if (!posts.isEmpty()) {
            this.crewPostSequence = posts.get(0).getCrewPostSequence();
            this.postDetails = posts.stream()
                    .map(post -> CrewRunPostResponse.builder()
                            .postId(post.getPostId())
                            .crewPostSequence(post.getCrewPostSequence())
                            .crewId(Math.toIntExact(post.getCrew().getCrewId()))
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
