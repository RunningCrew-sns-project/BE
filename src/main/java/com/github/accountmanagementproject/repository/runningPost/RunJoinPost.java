package com.github.accountmanagementproject.repository.runningPost;


import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.repository.runningPost.enums.PostType;
import com.github.accountmanagementproject.repository.runningPost.enums.RunJoinPostStatus;
import com.github.accountmanagementproject.repository.runningPost.image.RunJoinPostImage;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Builder(toBuilder = true) // 기존 빌더를 복사하여 업데이트 가능하도록 설정
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "run_join_post")
public class RunJoinPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "crew_post_sequence")
    private Integer crewPostSequence = 0;   // 크루 게시물 순번

    @Column(name = "general_post_sequence")
    private Integer generalPostSequence = 0; // 일반 게시물 순번

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id", nullable = true)
    private Crew crew;    // 크루에 속한 경우만 crew_id 포함

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private MyUser author;    // 작성자

    /** ****************************************************************/

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "max_participants")
    private Integer maxParticipants;  // 크루 모집 최대 인원

    @Enumerated(EnumType.STRING)
    @Column(name = "post_status", nullable = false)
    private RunJoinPostStatus status;  // 게시글 상태

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    private PostType postType;   // 게시자 분류

    @OneToMany(mappedBy = "runJoinPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RunJoinPostImage> joinPostImages;


    public void addJoinPostImage(RunJoinPostImage image) {
        joinPostImages.add(image);
        image.setRunJoinPost(this);  // 연관관계 설정
    }

    public void clearJoinPostImages() {
        for (RunJoinPostImage image : joinPostImages) {
            image.setRunJoinPost(null);  // 참조 해제
        }
        joinPostImages.clear();  // 고아 상태로 만들기
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
    private double distance;    // 총 거리

    /** ****************************************************************/

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();


    // 일반 게시물 순번 증가 메서드
    public void incrementGeneralPostSequence() {
        this.crewPostSequence = Optional.ofNullable(this.crewPostSequence).orElse(0) + 1;
    }

    // 크루 게시물 순번 증가 메서드 (필요한 경우)
    public void incrementCrewPostSequence() {
        this.crewPostSequence = Optional.ofNullable(this.crewPostSequence).orElse(0) + 1;
    }


}
