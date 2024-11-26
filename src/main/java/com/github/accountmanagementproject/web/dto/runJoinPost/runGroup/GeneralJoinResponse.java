package com.github.accountmanagementproject.web.dto.runJoinPost.runGroup;

import com.github.accountmanagementproject.repository.runningPost.enums.ParticipationStatus;
import com.github.accountmanagementproject.repository.runningPost.runGroup.RunGroup;
import com.github.accountmanagementproject.repository.runningPost.userRunGroups.UserRunGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;


@Builder
@AllArgsConstructor
@Getter
@Setter
public class GeneralJoinResponse {

    private Long runId;
    private boolean isGeneralPost;
    private String title;  // 달리기 게시물 제목

    private Long adminId;
    private String adminNickname;

    private Long userId;
    private String nickname;
    private String userEmail;
    private ParticipationStatus status;
    private String statusUpdatedAt;

    public static GeneralJoinResponse toDto(RunGroup runGroup) {
        return GeneralJoinResponse.builder()
                .runId(runGroup.getGeneralJoinPost().getGeneralPostId()) // 참여한 런 그룹 ID
                .isGeneralPost(true) // 일반 달리기 여부
                .title(runGroup.getGeneralJoinPost().getTitle()) // 게시글 제목
                .adminId(runGroup.getApprover() != null ? runGroup.getApprover().getUserId() : null) // 관리자 ID
                .adminNickname(runGroup.getApprover() != null ? runGroup.getApprover().getNickname() : null) // 관리자 닉네임
                .userId(runGroup.getUser().getUserId()) // 사용자 ID
                .nickname(runGroup.getUser().getNickname()) // 사용자 닉네임
                .userEmail(runGroup.getUser().getEmail()) // 사용자 이메일
                .status(runGroup.getStatus()) // 참여 상태
                .statusUpdatedAt(runGroup.getStatusUpdatedAt() != null
                        ? runGroup.getStatusUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        : null) // 상태 업데이트 시간
                .build();
    }

}
