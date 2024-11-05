package com.github.runningcrewsnsproject.web.dto.crew;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CrewDetailResponse {
    private String crewName;
    private String crewIntroduction;
    private String crewImageUrl;
    private String crewMaster;
    private String activityRegion;
    private LocalDateTime createdAt;
    private long memberCount;
    private int maxCapacity;


}
