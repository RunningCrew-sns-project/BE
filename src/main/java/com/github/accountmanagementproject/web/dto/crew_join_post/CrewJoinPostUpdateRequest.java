package com.github.accountmanagementproject.web.dto.crew_join_post;

import com.example.geoTest.model.crew_join_post.CrewJoinPost;
import com.example.geoTest.model.ex.User;
import com.example.geoTest.web.dto.DocumentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Builder
@Getter
@AllArgsConstructor
public class CrewJoinPostUpdateRequest {

//    private Long crewJoinPostId;            // 업데이트할 Post ID
//    private Integer crewId;          // 해당 크루 ID
    private String content;
    private Integer maxCrewNumber;
    private String startAddress;
    private String targetAddress;


    /**
     * Update
     * dto -> entity 로 변환
     */
    public static CrewJoinPost toEntity(
            CrewJoinPostUpdateRequest updateRequest,
            CrewJoinPost existingPost,
            User user,
            DocumentDto startAddressDto,
            DocumentDto targetAddressDto,
            double calculatedDistance) {

        return CrewJoinPost.builder()
                .id(existingPost.getId())
                .user(user)
                .content(updateRequest.getContent() != null ? updateRequest.getContent() : existingPost.getContent())
                .maxCrewNumber(updateRequest.getMaxCrewNumber() != null ? updateRequest.getMaxCrewNumber() : existingPost.getMaxCrewNumber())
                .inputAddress(updateRequest.getStartAddress() != null ? updateRequest.getStartAddress() : existingPost.getInputAddress())
                .inputLatitude(startAddressDto != null ? startAddressDto.getLatitude() : existingPost.getInputLatitude())
                .inputLongitude(startAddressDto != null ? startAddressDto.getLongitude() : existingPost.getInputLongitude())
                .targetAddress(updateRequest.getTargetAddress() != null ? updateRequest.getTargetAddress() : existingPost.getTargetAddress())
                .targetLatitude(targetAddressDto != null ? targetAddressDto.getLatitude() : existingPost.getTargetLatitude())
                .targetLongitude(targetAddressDto != null ? targetAddressDto.getLongitude() : existingPost.getTargetLongitude())
                .distance(calculatedDistance > 0 ? calculatedDistance : existingPost.getDistance())  // 계산된 거리 값 사용
                .createdAt(existingPost.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .status(existingPost.getStatus())
                .build();
    }

}
