package com.github.accountmanagementproject.repository.crew.crewuser;

import com.github.accountmanagementproject.service.mapper.converter.CrewsUsersStatusConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "crews_users")
@Getter
@Setter
@NoArgsConstructor
public class CrewsUsers {
    @EmbeddedId
    private CrewsUsersPk crewsUsersPk;
    @Convert(converter = CrewsUsersStatusConverter.class)
    private CrewsUsersStatus status;
    @Column(name = "join_date")
    private LocalDateTime joinDate;
    @Column(name = "application_date")
    private LocalDateTime applicationDate;
    @Column(name = "withdrawal_date")
    private LocalDateTime withdrawalDate;

    //경고 횟수
    @Column(name = "caveat")
    private Integer caveat;

    public CrewsUsers(CrewsUsersPk crewsUsersPk){
        this.crewsUsersPk = crewsUsersPk;
    }
    public CrewsUsers requestToJoin(){
        this.status = CrewsUsersStatus.WAITING;
        this.applicationDate = LocalDateTime.now();
        this.caveat = 0;
        return this;
    }
    public boolean duplicateRequest(){
        return this.status==CrewsUsersStatus.WAITING||this.status==CrewsUsersStatus.COMPLETED;
    }
    public boolean forcedExitOrWithdraw(){
        if(this.status==CrewsUsersStatus.WITHDRAWAL){
            LocalDateTime now = LocalDateTime.now();
            return now.isAfter(this.withdrawalDate.plusDays(1));
        }else if (this.status==CrewsUsersStatus.FORCED_EXIT){
            LocalDateTime now = LocalDateTime.now();
            return now.isAfter(this.withdrawalDate.plusDays(30));
        }
        return false;
    }
    public LocalDateTime getReleaseDay() {
        if (this.status == CrewsUsersStatus.WITHDRAWAL) {
            return this.withdrawalDate.plusDays(1);
        } else if (this.status == CrewsUsersStatus.FORCED_EXIT) {
            return this.withdrawalDate.plusDays(30);
        }
        return withdrawalDate;
    }
}

