package com.github.accountmanagementproject.web.dto.crew;

import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Setter
public class MyCrewResponse extends CrewResponseParent {
    private LocalDateTime requestOrCompletionDate;
    private CrewsUsersStatus status;
    private boolean isCrewMaster;

}
