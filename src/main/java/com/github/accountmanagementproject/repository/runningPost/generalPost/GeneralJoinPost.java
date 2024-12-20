package com.github.accountmanagementproject.repository.runningPost.generalPost;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.runningPost.enums.GeneralRunJoinPostStatus;
import com.github.accountmanagementproject.repository.runningPost.enums.PostType;
import com.github.accountmanagementproject.repository.runningPost.image.RunJoinPostImage;
import com.github.accountmanagementproject.repository.runningPost.runGroup.RunGroup;
import com.github.accountmanagementproject.repository.runningPost.userRunGroups.UserRunGroup;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "general_join_post")
public class GeneralJoinPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "general_post_id")
    private Long generalPostId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private MyUser author;    // 작성자

    @OneToMany(mappedBy = "id.generalJoinPost")
    private Set<UserRunGroup> participants = new HashSet<>();

    @OneToMany(mappedBy = "generalJoinPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RunGroup> participantz = new HashSet<>();

    public void addParticipant(RunGroup runGroup) {
        if (!participantz.contains(runGroup)) {
            participantz.add(runGroup);
            runGroup.setGeneralJoinPost(this);  // RunGroup의 setGeneralJoinPost 호출
        }
    }


    /** ****************************************************************/

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "location", nullable = false)
    private String location;   // 지역

    @Column(name = "max_participants")
    private Integer maximumPeople= 0;  // 크루 모집 최대 인원

    @Column(name = "current_people", nullable = false)
    private Integer currentPeople = 0;  // 현재 참여 인원 수 , 초기값 0으로 설정;

    @Column(name = "date")
    private LocalDate date;  // 모임 날짜, 날짜만 저장

    @Column(name = "start_time", nullable = true)
    private LocalTime startTime;  // 모임 시작 시간, 시간만 저장

    @Enumerated(EnumType.STRING)
    @Column(name = "post_status", nullable = false)
    private GeneralRunJoinPostStatus status;  // 게시글 상태

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    private PostType postType;   // 게시자 분류

    @Builder.Default  // Builder 패턴 사용 시 초기값 설정
    @OneToMany(mappedBy = "generalJoinPost", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RunJoinPostImage> generalJoinPostImages = new ArrayList<>();  // 초기화


    public void addJoinPostImage(RunJoinPostImage image) {
        generalJoinPostImages.add(image);
        image.setGeneralJoinPost(this);  // 연관관계 설정
    }

    public void clearJoinPostImages() {
        for (RunJoinPostImage image : generalJoinPostImages) {
            image.setGeneralJoinPost(null);  // 참조 해제
        }
        generalJoinPostImages.clear();  // 고아 상태로 만들기
    }

    /** ****************************************************************/

    // 시작 위치
    private String inputLocation;  // 시작 위치 검색어
    private double inputLatitude;
    private double inputLongitude;

    // 종료 위치
    private String targetLocation;  // 종료 위치 검색어
    private double targetLatitude;
    private double targetLongitude;

    // 경로 정보
    @Column(name = "distance")
    private double distance;    // 총 거리  TODO

    /** ****************************************************************/

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // 게시글 생성 시간

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();  // 게시글 수정 시간
}
