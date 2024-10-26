package com.github.accountmanagementproject.repository.crew.crewsUsers;

import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.crew.crews.Crew;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Embeddable
public class CrewsUsersPk {
    @ManyToOne
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private MyUser user;
}
