package com.github.accountmanagementproject.web.dto.crew;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.accountmanagementproject.web.dto.infinitescrolling.ScrollingResponseInterface;
import com.github.accountmanagementproject.web.dto.infinitescrolling.criteria.CursorHolder;
import com.github.accountmanagementproject.web.dto.infinitescrolling.criteria.SearchCriteria;
import com.github.accountmanagementproject.web.dto.infinitescrolling.criteria.SearchRequest;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CrewListResponse extends CrewResponseParent implements ScrollingResponseInterface<SearchCriteria> {
    private String activityRegion;
    private LocalDateTime createdAt;
    private long memberCount;
    private int maxCapacity;
    @JsonIgnore
    private Long popularOrActivitiesNumerical;

    @Override
    public String nextCursor(SearchCriteria criteria) {
        return switch (criteria) {
            case POPULAR,ACTIVITIES ->  this.popularOrActivitiesNumerical.toString();
            case NAME -> super.getCrewName();
            case MEMBER -> String.valueOf(memberCount);
            case LATEST -> createdAt.toString();
        };
    }
    @Override
    public long nextCursorId(){
        return super.getCrewId();
    }

    public CrewListResponse(long crewId, String crewName, String crewImageUrl, String crewIntroduction, String activityRegion, LocalDateTime createdAt, long memberCount, int maxCapacity, Long popularOrActivitiesNumerical) {
        this.activityRegion = activityRegion;
        this.createdAt = createdAt;
        this.memberCount = memberCount;
        this.maxCapacity = maxCapacity;
        this.popularOrActivitiesNumerical = popularOrActivitiesNumerical;
        super.setCrewIntroduction(crewIntroduction);
        super.setCrewName(crewName);
        super.setCrewId(crewId);
        super.setCrewImageUrl(crewImageUrl);
    }

    public boolean valueValidity(long requestId, SearchRequest requestCursor){
        CursorHolder cursor = requestCursor.getCursorHolder();
        return switch (requestCursor.getSearchCriteria()) {
            case NAME -> super.getCrewName().equals(cursor.getNameCursor())&&super.getCrewId()==requestId;
            case MEMBER -> this.memberCount==cursor.getMemberCursor()&&super.getCrewId()==requestId;
            case POPULAR,ACTIVITIES -> this.popularOrActivitiesNumerical.equals(cursor.getPopularOrActivitiesCursor())&&super.getCrewId()==requestId;
            case LATEST -> this.createdAt.equals(cursor.getCreatedAtCursor())&&super.getCrewId()==requestId;
        };
    }
}
