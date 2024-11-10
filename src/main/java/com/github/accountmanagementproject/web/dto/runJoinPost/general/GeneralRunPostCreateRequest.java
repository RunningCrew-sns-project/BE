package com.github.accountmanagementproject.web.dto.runJoinPost.general;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.runningPost.enums.GeneralRunJoinPostStatus;
import com.github.accountmanagementproject.repository.runningPost.enums.PostType;
import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPost;
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
public class GeneralRunPostCreateRequest {

    @NotBlank(message = "게시물 제목을 입력해주세요.")
    private String title; // 게시물 제목

    @NotBlank(message = "게시물 내용을 입력해주세요.")
    private String content; // 게시물 내용

    @NotNull(message = "최대 참여 인원.")
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


    public static GeneralJoinPost toEntity(GeneralRunPostCreateRequest request, MyUser user) {
        GeneralJoinPost post = GeneralJoinPost.builder()
                .author(user)
                .title(request.getTitle())
                .content(request.getContent())
                .location(request.getLocation()) // 추가된 location 속성
                .maximumPeople(request.getMaximumPeople())
                .date(request.getDate()) // 추가된 date 속성
                .startTime(request.getStartTime() != null ? request.getStartTime() : null) // 추가된 startTime 속성
                .inputLocation(request.getInputLocation())
                .inputLatitude(request.getInputLatitude())
                .inputLongitude(request.getInputLongitude())
                .targetLocation(request.getTargetLocation())
                .targetLatitude(request.getTargetLatitude())
                .targetLongitude(request.getTargetLongitude())
                .status(GeneralRunJoinPostStatus.OPEN)
                .postType(PostType.GENERAL)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build();

        return post;
    }

}
