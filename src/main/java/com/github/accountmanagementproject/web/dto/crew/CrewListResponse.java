package com.github.accountmanagementproject.web.dto.crew;

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

    @Override
    public String nextCursor(SearchCriteria criteria) {
        return switch (criteria) {
            case NAME -> super.getCrewName();
            case MEMBER -> String.valueOf(memberCount);
            default -> createdAt.toString();
        };
    }
    @Override
    public long nextCursorId(){
        return super.getCrewId();
    }

    public CrewListResponse(long crewId, String crewName, String crewImageUrl, String crewIntroduction, String activityRegion, LocalDateTime createdAt, long memberCount, int maxCapacity) {
        this.activityRegion = activityRegion;
        this.createdAt = createdAt;
        this.memberCount = memberCount;
        this.maxCapacity = maxCapacity;
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
            default -> this.createdAt.equals(cursor.getCreatedAtCursor())&&super.getCrewId()==requestId;
        };
    }
}
