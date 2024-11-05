package com.github.runningcrewsnsproject.repository.crew.crewuser;

import com.github.runningcrewsnsproject.repository.account.user.MyUser;
import com.github.runningcrewsnsproject.repository.crew.crew.Crew;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Embeddable
public class CrewsUsersPk {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private MyUser user;
}
