package com.github.accountmanagementproject.web.dto.crew;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Setter
public class MyCrewResponse {
    private long crewId;
    private String crewName;
    private String crewImageUrl;
    private String crewIntroduction;
    private LocalDateTime requestOrCompletionDate;
    private boolean isCrewMaster;
}
