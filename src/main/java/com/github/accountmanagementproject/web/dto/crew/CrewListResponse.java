package com.github.accountmanagementproject.web.dto.crew;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CrewListResponse {
    private Long crewId;
    private String crewName;
    private String crewIntroduction;
    private List<String> crewImageUrls;
    private String crewMaster;
    private String activityRegion;
    private LocalDateTime createdAt;
    private long memberCount;
    private int maxCapacity;


}
