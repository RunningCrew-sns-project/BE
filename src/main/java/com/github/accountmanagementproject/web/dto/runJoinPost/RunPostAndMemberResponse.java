package com.github.accountmanagementproject.web.dto.runJoinPost;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.accountmanagementproject.web.dto.runJoinPost.runmember.RunMemberResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RunPostAndMemberResponse {
    private Long runPostId;
    private String title;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RunMemberResponse runMaster;
    private List<RunMemberResponse> runMemberResponses;
    private int maxParticipant;
    private String location;
    private long currentParticipant;
}
