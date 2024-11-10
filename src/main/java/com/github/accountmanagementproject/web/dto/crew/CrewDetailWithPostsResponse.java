package com.github.accountmanagementproject.web.dto.crew;


import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CrewDetailWithPostsResponse {

    private CrewListResponse crewInfo;
    private List<CrewRunPostResponse> items;
}