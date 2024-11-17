package com.github.accountmanagementproject.web.dto.runJoinPost.crewRunGroup;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.runningPost.crewRunGroup.CrewRunGroup;
import com.github.accountmanagementproject.repository.runningPost.enums.ParticipationStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CrewRunJoinResponse {

    private Long runId;
    private boolean isCrewRunPost;
    private String title;  // 크루 달리기 게시물 제목

    private Long adminId;
    private String adminNickname;

    private Long userId;
    private String nickname;
    private String userEmail;
    private ParticipationStatus status;
    private String statusUpdatedAt;


    public static CrewRunJoinResponse toDto(CrewRunGroup crewRunGroup) {
        return CrewRunJoinResponse.builder()
                .runId(crewRunGroup.getCrewJoinPost().getCrewPostId())
                .title(crewRunGroup.getCrewJoinPost().getTitle())
                .isCrewRunPost(true)  // 크루 달리기 게시물이므로 true로 설정
                .adminId(crewRunGroup.getApprover() != null ? crewRunGroup.getApprover().getUserId() : null)
                .adminNickname(crewRunGroup.getApprover() != null ? crewRunGroup.getApprover().getNickname() : null)
                .userId(crewRunGroup.getUser().getUserId())
                .nickname(crewRunGroup.getUser().getNickname())
                .userEmail(crewRunGroup.getUser().getEmail())
                .status(crewRunGroup.getStatus())
                .statusUpdatedAt(crewRunGroup.getJoinedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

}
