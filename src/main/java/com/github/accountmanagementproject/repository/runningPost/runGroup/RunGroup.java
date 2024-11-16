package com.github.accountmanagementproject.repository.runningPost.runGroup;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPost;
import com.github.accountmanagementproject.repository.runningPost.userRunGroups.ParticipationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "run_group")
public class RunGroup {

    @EmbeddedId
    private RunGroupId id = new RunGroupId();

    @MapsId("userId")  // RunGroupId의 userId 필드와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private MyUser user;      // 참여 신청자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private MyUser approver;  // 승인해주는 사람 (방장)

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("generalPostId")  // 복합 키의 generalPostId와 매핑
    @JoinColumn(name = "general_post_id", nullable = false)
    private GeneralJoinPost generalJoinPost;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ParticipationStatus status = ParticipationStatus.PENDING;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "status_updated_at")
    private LocalDateTime statusUpdatedAt = LocalDateTime.now();


    // 연관 관계 편의 메서드
    public void setGeneralJoinPost(GeneralJoinPost generalJoinPost) {
        this.generalJoinPost = generalJoinPost;
        if (generalJoinPost != null && !generalJoinPost.getParticipants().contains(this)) {
            generalJoinPost.addParticipant(this);
        }
    }

}
