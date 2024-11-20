package com.github.accountmanagementproject.repository.runningPost.crewRunGroup;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Setter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CrewRunGroupId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "crew_post_id")
    private Long crewPostId;
}
