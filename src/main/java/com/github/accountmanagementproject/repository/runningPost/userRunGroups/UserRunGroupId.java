package com.github.accountmanagementproject.repository.runningPost.userRunGroups;

import jakarta.persistence.Embeddable;
import lombok.*;


@Getter
@Setter
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserRunGroupId {

    private Long userId;
    private Long crewPostId;
    private Long generalPostId;

}
