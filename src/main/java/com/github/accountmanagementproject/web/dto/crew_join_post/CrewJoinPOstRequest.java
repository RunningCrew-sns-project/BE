package com.github.accountmanagementproject.web.dto.crew_join_post;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class CrewJoinPOstRequest {

    private String content;
    private Integer maxCrewNumber;

    private String startAddress;
    private String targetAddress;
}
