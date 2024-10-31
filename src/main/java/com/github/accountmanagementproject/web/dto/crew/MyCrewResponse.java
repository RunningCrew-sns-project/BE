package com.github.accountmanagementproject.web.dto.crew;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
@AllArgsConstructor
public class MyCrewResponse {
    private String crewName;
    private String crewDescription;
    private String crewImageUrl;
}
