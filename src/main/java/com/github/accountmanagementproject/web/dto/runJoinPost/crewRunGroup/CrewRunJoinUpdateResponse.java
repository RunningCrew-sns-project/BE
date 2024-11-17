package com.github.accountmanagementproject.web.dto.runJoinPost.crewRunGroup;


import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.runningPost.enums.ParticipationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CrewRunJoinUpdateResponse {

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


    // 승인 상태 업데이트를 위한 새로운 메서드 추가
    public void updateApprovalStatus(MyUser admin, ParticipationStatus status, LocalDateTime statusUpdatedAt) {
        this.adminId = admin.getUserId();
        this.adminNickname = admin.getNickname();
        this.status = status;
        this.statusUpdatedAt = statusUpdatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


    public static CrewRunJoinUpdateResponse fromEntity(CrewRunJoinResponse existing,
                                                 MyUser admin,
                                                 ParticipationStatus newStatus,
                                                 LocalDateTime updateTime) {
        CrewRunJoinUpdateResponse response = new CrewRunJoinUpdateResponse();
        response.setRunId(existing.getRunId());
        response.setTitle(existing.getTitle());
        response.setCrewRunPost(existing.isCrewRunPost());
        response.setUserId(existing.getUserId());
        response.setNickname(existing.getNickname());
        response.setUserEmail(existing.getUserEmail());
        // 업데이트되는 필드들
        response.setAdminId(admin.getUserId());
        response.setAdminNickname(admin.getNickname());
        response.setStatus(newStatus);
        response.setStatusUpdatedAt(updateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return response;
    }

}
