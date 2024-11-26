package com.github.accountmanagementproject.web.dto.crew;

import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class CrewJoinResponse {
    private String crewName;
    private CrewsUsersStatus status;
    private String applicationDate;
    private boolean isJoinCompleted;
    public CrewJoinResponse(String crewName, CrewsUsersStatus status, LocalDateTime applicationDate) {
        this.crewName = crewName;
        this.status = status;
        this.applicationDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(applicationDate);
        this.isJoinCompleted = status.equals(CrewsUsersStatus.COMPLETED);
    }
}
