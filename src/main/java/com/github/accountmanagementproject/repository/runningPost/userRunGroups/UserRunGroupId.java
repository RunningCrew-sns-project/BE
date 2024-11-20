package com.github.accountmanagementproject.repository.runningPost.userRunGroups;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPost;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;


@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserRunGroupId {

//    @Column(name = "user_id")
//    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private MyUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "general_post_id", nullable = false)
    private GeneralJoinPost generalJoinPost;

    @Column(name = "crew_post_id")
    private Long crewPostId;

    public UserRunGroupId(MyUser participant, GeneralJoinPost post) {
        this.user = participant;
        this.generalJoinPost = post;
        this.crewPostId = null;  // 일반 게시물의 경우
    }

//    @Column(name = "general_post_id")
//    private Long generalPostId;

//    public UserRunGroupId(Long userId, Long generalPostId) {
//        this.userId = userId;
//        this.generalPostId = generalPostId;
//        this.crewPostId = null;  // 일반 게시물의 경우
//    }




}
