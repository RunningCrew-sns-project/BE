package com.github.accountmanagementproject.web.dto.crew;

import com.github.accountmanagementproject.web.dto.infinitescrolling.ScrollingResponseInterface;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CrewListResponse extends CrewResponseParent implements ScrollingResponseInterface {
    private String activityRegion;
    private LocalDateTime createdAt;
    private long memberCount;
    private int maxCapacity;

    @Override
    public Long getId() {
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
}
