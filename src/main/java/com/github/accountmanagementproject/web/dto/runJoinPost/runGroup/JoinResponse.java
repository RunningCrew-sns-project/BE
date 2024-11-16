package com.github.accountmanagementproject.web.dto.runJoinPost.runGroup;

import com.github.accountmanagementproject.repository.runningPost.runGroup.RunGroup;
import com.github.accountmanagementproject.repository.runningPost.userRunGroups.ParticipationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;


@Builder
@AllArgsConstructor
@Getter
@Setter
public class JoinResponse {

    private String nickname;
    private String userEmail;
    private ParticipationStatus status;
    private String requestedDate;


    public static JoinResponse toDto(RunGroup runGroup) {
        if (runGroup == null || runGroup.getUser() == null || runGroup.getGeneralJoinPost() == null) {
            throw new IllegalArgumentException("Invalid RunGroup or missing fields");
        }

        return new JoinResponse(
                runGroup.getUser().getNickname(),                // 사용자 닉네임
                runGroup.getUser().getEmail(),                   // 사용자 이메일
                runGroup.getStatus(),                            // 참여 상태
                runGroup.getJoinedAt().toString()
        );
    }


}
