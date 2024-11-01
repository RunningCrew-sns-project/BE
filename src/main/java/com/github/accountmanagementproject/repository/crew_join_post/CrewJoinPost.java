package com.github.accountmanagementproject.repository.crew_join_post;

import com.github.accountmanagementproject.repository.account.users.MyUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "crew_join_post")
public class CrewJoinPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Users와의 다대일 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private MyUser user;  // userId 대신 User 엔티티로 변경

    // Crews와의 다대일 관계 설정
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "crew_id")
//    private Crew crew;  // 추가 예정

//    @Column(name = "crew_name", nullable = false)
//    private String crewName;  // 게시자 이름 : User 의 nickname 과 메핑 -> response 에서 보여줄 예정

    @Column(name = "content", nullable = false)
    private String content;  // 게시글 내용

    @Column(name = "max_crew_number", nullable = false)
    private Integer maxCrewNumber;  // 크루 모집 최대 인원

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CrewJoinPostStatus status;  // 게시글 상태

    @Column(name = "image")
    private String image;     // 이미지 URL

    /** ****************************************************************/

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** ****************************************************************/

    // 시작 지점 정보
    @Column(name = "start_location_name")
    private String startLocationName;  // 출발 장소명

    @Column(name = "start_address")
    private String startAddress;      // 출발 주소

    @Column(name = "start_latitude", precision = 10, scale = 7)
    private BigDecimal startLatitude;  // 출발 위도

    @Column(name = "start_longitude", precision = 10, scale = 7)
    private BigDecimal startLongitude; // 출발 경도

    // 도착 지점 정보
    @Column(name = "end_location_name")
    private String endLocationName;    // 도착 장소명

    @Column(name = "end_address")
    private String endAddress;        // 도착 주소

    @Column(name = "end_latitude", precision = 10, scale = 7)
    private BigDecimal endLatitude;    // 도착 위도

    @Column(name = "end_longitude", precision = 10, scale = 7)
    private BigDecimal endLongitude;   // 도착 경도

    // 경로 정보
    @Column(name = "distance_meters")
    private double distance;    // 총 거리


}
