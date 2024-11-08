package com.github.accountmanagementproject.repository.runningPost.crewPost;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.repository.runningPost.enums.CrewRunJoinPostStatus;
import com.github.accountmanagementproject.repository.runningPost.enums.PostType;
import com.github.accountmanagementproject.repository.runningPost.image.RunJoinPostImage;
import com.github.accountmanagementproject.repository.runningPost.userRunGroups.UserRunGroup;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Builder(toBuilder = true) // 기존 빌더를 복사하여 업데이트 가능하도록 설정
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "crew_join_post")
public class CrewJoinPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crew_post_id")
    private Long crewPostId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id", nullable = true)
    private Crew crew;    // 크루에 속한 경우만 crew_id 포함

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private MyUser author;    // 작성자

    @OneToMany(mappedBy = "crewJoinPost", fetch = FetchType.EAGER)
    private Set<UserRunGroup> participants = new HashSet<>();

    /** ****************************************************************/

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "location", nullable = false)
    private String location;   // 지역

    @Column(name = "max_participants")
    private Integer maximumPeople ;  // 크루 모집 최대 인원

    @Column(name = "current_people", nullable = false)
    private Integer currentPeople; // 현재 참여 인원 수

    @Column(name = "date")
    private LocalDate date;  // 모임 날짜, 날짜만 저장

    @Column(name = "start_time", nullable = true)
    private LocalTime startTime;  // 모임 시작 시간, 시간만 저장

    @Enumerated(EnumType.STRING)
    @Column(name = "post_status", nullable = false)
    private CrewRunJoinPostStatus status;  // 게시글 상태

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    private PostType postType;   // 게시자 분류

    @OneToMany(mappedBy = "crewJoinPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RunJoinPostImage> crewJoinPostImages;


    public void addJoinPostImage(RunJoinPostImage image) {
        crewJoinPostImages.add(image);
        image.setCrewJoinPost(this);  // 연관관계 설정
    }

    public void clearJoinPostImages() {
        for (RunJoinPostImage image : crewJoinPostImages) {
            image.setCrewJoinPost(null);  // 참조 해제
        }
        crewJoinPostImages.clear();  // 고아 상태로 만들기
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

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // 게시글 생성 시간

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();  // 게시글 수정 시간

}
