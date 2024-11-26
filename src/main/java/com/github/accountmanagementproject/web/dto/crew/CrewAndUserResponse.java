package com.github.accountmanagementproject.web.dto.crew;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class CrewAndUserResponse {
    private Long crewId;
    private String crewName;
    private int maxCapacity;
    private long memberCount;
    private List<SimplyCrewUserResponse> crewUserResponses;
}
