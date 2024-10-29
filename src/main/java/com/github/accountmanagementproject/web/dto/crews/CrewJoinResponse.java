package com.github.accountmanagementproject.web.dto.crews;

import com.github.accountmanagementproject.repository.crew.crewsUsers.CrewsUsersStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrewJoinResponse {
    private String crewName;
    private CrewsUsersStatus status;
    private String applicationDate;
    private boolean isJoinCompleted;
}
