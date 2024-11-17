package com.github.accountmanagementproject.repository.runningPost.crewRunGroup;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;
import com.github.accountmanagementproject.repository.runningPost.enums.ParticipationStatus;
import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPost;
import com.github.accountmanagementproject.repository.runningPost.runGroup.RunGroupId;
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
@Table(name = "crew_run_group")
public class CrewRunGroup {

    @EmbeddedId
    private CrewRunGroupId id = new CrewRunGroupId();

    @MapsId("userId")  // RunGroupId의 userId 필드와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private MyUser user;      // 참여 신청자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private MyUser approver;  // 승인해주는 사람 (방장)

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("crewPostId")  // 복합 키의 crewPostId와 매핑
    @JoinColumn(name = "crew_post_id", nullable = false)
    private CrewJoinPost crewJoinPost;

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
    public void setGeneralJoinPost(CrewJoinPost crewJoinPost) {
        this.crewJoinPost = crewJoinPost;
        if (crewJoinPost != null && !crewJoinPost.getParticipants().contains(this)) {
            crewJoinPost.addParticipant(this);
        }
    }

}
