package com.github.accountmanagementproject.web.dto.runJoinPost.crew;

import com.github.accountmanagementproject.repository.runningPost.crewRunGroup.CrewRunGroup;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralParticipantsResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class CrewParticipantsResponse {

    private Long userId;
    private String nickname;
    private Long adminId;
    private String status;
    private LocalDateTime joinedAt;
    private LocalDateTime statusUpdatedAt;


    public static CrewParticipantsResponse toDto(CrewRunGroup crewRunGroup) {
        return CrewParticipantsResponse.builder()
                .userId(crewRunGroup.getUser().getUserId())
                .nickname(crewRunGroup.getUser().getNickname())
                .adminId(crewRunGroup.getApprover() != null ? crewRunGroup.getApprover().getUserId() : null)
                .status(crewRunGroup.getStatus().name())
                .joinedAt(crewRunGroup.getJoinedAt())
                .statusUpdatedAt((crewRunGroup.getStatusUpdatedAt()))
                .build();
    }
}
