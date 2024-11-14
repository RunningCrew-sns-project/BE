package com.github.accountmanagementproject.web.dto.account.crew;

import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserAboutCrew {
    private CrewsUsersStatus status;
    private LocalDateTime joinDate;
    private LocalDateTime applicationDate;
    private LocalDateTime withdrawalDate;
    private int caveat;
    private Boolean isMaster;
}
