package com.github.accountmanagementproject.web.dto.account.crew;

import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class UserAboutCrew {
    private CrewsUsersStatus status;
    private LocalDateTime joinDate;
    private LocalDateTime applicationDate;
    private LocalDateTime withdrawalDate;
    private int caveat;
    private Boolean isMaster;

    public LocalDate getReleaseDay() {
        if(isMaster) return null;
        return switch (this.status) {
            case WITHDRAWAL -> this.withdrawalDate.plusDays(2).toLocalDate();
            case FORCED_EXIT -> this.withdrawalDate.plusDays(31).toLocalDate();
            case REJECTED -> this.withdrawalDate.plusDays(8).toLocalDate();
            default -> null;
        };
    }
    public boolean isAvailableToJoin() {

        return this.statusIs() &&
                (LocalDate.now().isEqual(this.getReleaseDay()) || LocalDate.now().isAfter(this.getReleaseDay()));
    }
    private boolean statusIs() {
        if(isMaster) return false;
        return !(this.status == CrewsUsersStatus.WAITING || this.status == CrewsUsersStatus.COMPLETED);
    }
}
