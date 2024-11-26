package com.github.accountmanagementproject.web.dto.runJoinPost.runmember;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.accountmanagementproject.repository.account.user.myenum.Gender;
import com.github.accountmanagementproject.repository.runningPost.enums.ParticipationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RunMemberResponse {
    @JsonIgnore
    private long postId;
    private long userId;
    private String nickname;
    private String profileImg;
    private String profileMessage;
    private Gender gender;
    private String phoneNumber;
    private ParticipationStatus status;
    private LocalDateTime applicationDate;
}
