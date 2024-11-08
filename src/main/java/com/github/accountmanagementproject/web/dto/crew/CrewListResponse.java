package com.github.accountmanagementproject.web.dto.crew;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CrewListResponse extends CrewResponseParent{
    private String activityRegion;
    private LocalDateTime createdAt;
    private long memberCount;
    private int maxCapacity;

}
