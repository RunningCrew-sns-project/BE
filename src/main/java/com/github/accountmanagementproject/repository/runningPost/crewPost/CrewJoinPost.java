package com.github.accountmanagementproject.repository.runningPost.crewPost;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.repository.runningPost.crewRunGroup.CrewRunGroup;
import com.github.accountmanagementproject.repository.runningPost.enums.CrewRunJoinPostStatus;
import com.github.accountmanagementproject.repository.runningPost.enums.PostType;
import com.github.accountmanagementproject.repository.runningPost.image.CrewJoinPostImage;
import com.github.accountmanagementproject.repository.runningPost.image.RunJoinPostImage;
import com.github.accountmanagementproject.repository.runningPost.runGroup.RunGroup;
import com.github.accountmanagementproject.repository.runningPost.userRunGroups.UserRunGroup;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;


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

    @OneToMany(mappedBy = "crewJoinPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CrewRunGroup> participants = new HashSet<>();

    // 연관 관계 편의 메서드
    public void addParticipant(CrewRunGroup crewRunGroup) {
        if (!participants.contains(crewRunGroup)) {
            participants.add(crewRunGroup);
            crewRunGroup.setGeneralJoinPost(this);  // RunGroup의 setGeneralJoinPost 호출
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

    @Builder.Default // 이 부분 추가, 빌더 패턴 사용 시에도 ArrayList가 초기화
    @OneToMany(mappedBy = "crewJoinPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CrewJoinPostImage> crewJoinPostImages = new ArrayList<>();  // 초기화


    public void addJoinPostImage(CrewJoinPostImage image) {
        crewJoinPostImages.add(image);
        image.setCrewJoinPost(this);  // 연관관계 설정
    }

    public void clearJoinPostImages() {
        for (CrewJoinPostImage image : crewJoinPostImages) {
            image.setCrewJoinPost(null);  // 참조 해제
        }
        crewJoinPostImages.clear();  // 고아 상태로 만들기
    }

//    @Transactional
//    public void clearJoinPostImages() {
//        Iterator<CrewJoinPostImage> iterator = crewJoinPostImages.iterator();
//        while (iterator.hasNext()) {
//            CrewJoinPostImage image = iterator.next();
//            image.setCrewJoinPost(null);
//            iterator.remove();
//        }
//    }

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
