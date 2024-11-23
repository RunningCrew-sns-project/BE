package com.github.accountmanagementproject.web.dto.runJoinPost.general;

import com.github.accountmanagementproject.repository.runningPost.runGroup.RunGroup;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class GeneralParticipantsResponse {

    private Long userId;
    private String nickname;
    private Long adminId;
    private String adminNickname;
    private String status;
    private LocalDateTime joinedAt;
    private LocalDateTime statusUpdatedAt;


    public static GeneralParticipantsResponse toDto(RunGroup runGroup) {
        return GeneralParticipantsResponse.builder()
                .userId(runGroup.getUser().getUserId())
                .nickname(runGroup.getUser().getNickname())
                .adminId(runGroup.getApprover() != null ? runGroup.getApprover().getUserId() : null)
                .adminNickname(runGroup.getApprover() != null ? runGroup.getApprover().getNickname() : null)
                .status(runGroup.getStatus().name())
                .joinedAt(runGroup.getJoinedAt())
                .statusUpdatedAt((runGroup.getStatusUpdatedAt()))
                .build();
    }
}
