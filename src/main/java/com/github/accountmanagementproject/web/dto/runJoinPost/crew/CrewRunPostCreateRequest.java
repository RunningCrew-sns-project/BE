package com.github.accountmanagementproject.web.dto.runJoinPost.crew;


import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;
import com.github.accountmanagementproject.repository.runningPost.enums.CrewRunJoinPostStatus;
import com.github.accountmanagementproject.repository.runningPost.enums.PostType;
import com.github.accountmanagementproject.web.dto.storage.FileDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrewRunPostCreateRequest {

    @NotBlank(message = "게시물 제목을 입력해주세요.")
    private String title; // 게시물 제목

    @NotBlank(message = "게시물 내용을 입력해주세요.")
    private String content; // 게시물 내용

    @NotNull(message = "최대 참여 인원")
    private Integer maximumPeople; // 최대 참여 인원

    private String location;

    private LocalDate date;

    private LocalTime startTime;

    // 위치 정보
    private String inputLocation; // 시작 위치
    private double inputLatitude; // 시작 위도
    private double inputLongitude; // 시작 경도

    private String targetLocation; // 종료 위치
    private double targetLatitude; // 종료 위도
    private double targetLongitude; // 종료 경도

    private List<FileDto> fileDtos;  // 파일 이미지


    public static CrewJoinPost toEntity(CrewRunPostCreateRequest request, MyUser user, Crew crew) {
        CrewJoinPost post = CrewJoinPost.builder()
                .author(user)  // 크루 마스터일 수도 있고, 크루에 승인된 일반 회원일 수도 있음
                .crew(crew)   // RunJoinPost 가 작성될 크루 (크루가 이미 가입 승인된 상태임)
                .title(request.getTitle())
                .content(request.getContent())
                .location(request.getLocation())
                .maximumPeople(request.getMaximumPeople())
                .currentPeople(0)  // 처음 게시글 생성 시 참여 인원은 0명으로 설정
                .date(request.getDate())
                .startTime(request.getStartTime() != null ? request.getStartTime() : null)
                .inputLocation(request.getInputLocation())
                .inputLatitude(request.getInputLatitude())
                .inputLongitude(request.getInputLongitude())
                .targetLocation(request.getTargetLocation())
                .targetLatitude(request.getTargetLatitude())
                .targetLongitude(request.getTargetLongitude())
                .status(CrewRunJoinPostStatus.OPEN)
                .postType(PostType.CREW)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build();

        return post;
    }
}
