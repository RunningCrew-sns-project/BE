package com.github.accountmanagementproject.web.dto.crew;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CrewResponseParent {
    private long crewId;
    private String crewName;
    private String crewImageUrl;
    private String crewIntroduction;
}
