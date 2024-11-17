package com.github.accountmanagementproject.repository.runningPost.userRunGroups;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;
import com.github.accountmanagementproject.repository.runningPost.enums.ParticipationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;


@Builder
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_run_group")
public class UserRunGroup implements Serializable {

    @EmbeddedId
    private UserRunGroupId id = new UserRunGroupId();

//    @MapsId("userId")  // UserRunGroupId의 userId 필드와 매핑
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private MyUser user;        // 참여 신청한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private MyUser admin;      // 방장 (마스터)

    @ManyToOne
    @JoinColumn(name = "crew_post_id", insertable = false, updatable = false)
    private CrewJoinPost crewJoinPost;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @MapsId("generalPostId")  // generalPostId만 @MapsId 사용
//    @JoinColumn(name = "general_post_id", insertable = false, updatable = false)
//    private GeneralJoinPost generalJoinPost;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ParticipationStatus status = ParticipationStatus.PENDING;


    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime joinedAt = LocalDateTime.now();       // 참여 신청일

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime statusUpdatedAt = LocalDateTime.now(); // 상태 변경일

    private String statusChangeReason;                          // 상태 변경 사유 (거절/강퇴 사유)


    // 연관관계 편의 메서드
//    public void setUser(MyUser user) {
//        this.id.setUserId(user.getUserId());
//    }
//
//    public void setGeneralJoinPost(GeneralJoinPost post) {
//        this.id.setGeneralPostId(post.getGeneralPostId());
//    }


}
