package com.github.accountmanagementproject.repository.crew_join_post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.crew.crews.Crew;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "crew_join_posts")
@JsonIgnoreProperties({"crew"})  // 순환 참조 방지
public class CrewJoinPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crew_join_post_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private MyUser user;  // userId 대신 User 엔티티로 변경

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @Column(name = "content", nullable = false)
    private String content;  // 게시글 내용

    @Column(name = "max_crew_number", nullable = false)
    private Integer maxCrewNumber;  // 크루 모집 최대 인원

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CrewJoinPostStatus status;  // 게시글 상태

    /** ****************************************************************/

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** ****************************************************************/

    // 시작 위치
    private String inputAddress;
    private double inputLatitude;
    private double inputLongitude;

    // 종료 위치
    private String targetAddress;
    private double targetLatitude;
    private double targetLongitude;

    // 경로 정보
    @Column(name = "distance")
    private double distance;    // 총 거리



    public CrewJoinPost(MyUser user, Crew crew, String content, Integer maxCrewNumber,
                        CrewJoinPostStatus status, LocalDateTime createdAt, String inputAddress,
                        double inputLatitude, double inputLongitude,
                        String targetAddress, double targetLatitude, double targetLongitude,
                        double distance) {
        this.user = user;
        this.crew = crew;
        this.content = content;
        this.maxCrewNumber = maxCrewNumber;
        this.status = status;
        this.createdAt = createdAt;
        this.inputAddress = inputAddress;
        this.inputLatitude = inputLatitude;
        this.inputLongitude = inputLongitude;
        this.targetAddress = targetAddress;
        this.targetLatitude = targetLatitude;
        this.targetLongitude = targetLongitude;
        this.distance = distance;
    }
}
