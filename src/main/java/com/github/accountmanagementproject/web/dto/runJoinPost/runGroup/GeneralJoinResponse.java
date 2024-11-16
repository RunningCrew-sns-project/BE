package com.github.accountmanagementproject.web.dto.runJoinPost.runGroup;

import com.github.accountmanagementproject.repository.runningPost.runGroup.RunGroup;
import com.github.accountmanagementproject.repository.runningPost.userRunGroups.ParticipationStatus;
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

    private String nickname;
    private String userEmail;
    private ParticipationStatus status;
    private String requestedDate;

    public static GeneralJoinResponse from(UserRunGroup userRunGroup) {
        return GeneralJoinResponse.builder()
                .nickname(userRunGroup.getId().getUser().getNickname())
                .userEmail(userRunGroup.getId().getUser().getEmail())
                .status(userRunGroup.getStatus())
                .requestedDate(userRunGroup.getJoinedAt()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

}
