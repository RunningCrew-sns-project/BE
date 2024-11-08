package com.github.accountmanagementproject.repository.runningPost.userRunGroups;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;
import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPost;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_run_group")
public class UserRunGroup {

    @EmbeddedId
    private UserRunGroupId id = new UserRunGroupId();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private MyUser user;

    @ManyToOne
    @MapsId("crewPostId")
    @JoinColumn(name = "crew_post_id")
    private CrewJoinPost crewJoinPost;

    @ManyToOne
    @MapsId("generalPostId")
    @JoinColumn(name = "general_post_id")
    private GeneralJoinPost generalJoinPost;


    private LocalDateTime joinedAt = LocalDateTime.now();  // 참여일

}
