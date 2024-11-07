package com.github.accountmanagementproject.web.dto.runJoinPost.general;


import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.runningPost.RunJoinPost;
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
public class GeneralRunPostUpdateRequest {

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


    public GeneralJoinPost updateEntity(GeneralJoinPost post, MyUser user) {
        post.setAuthor(user);
        post.setTitle(this.title != null ? this.title : post.getTitle());
        post.setContent(this.content != null ? this.content : post.getContent());
        post.setMaximumPeople(this.maximumPeople != null ? this.maximumPeople : post.getMaximumPeople());
        post.setLocation(this.location != null ? this.location : post.getLocation()); // 추가된 location 속성
        post.setDate(this.date != null ? this.date : post.getDate()); // 추가된 date 속성
        post.setStartTime(this.startTime != null ? this.startTime : post.getStartTime()); // 추가된 startTime 속성
        post.setInputLocation(this.inputLocation != null ? this.inputLocation : post.getInputLocation());
        post.setInputLatitude(this.inputLatitude != 0 ? this.inputLatitude : post.getInputLatitude());
        post.setInputLongitude(this.inputLongitude != 0 ? this.inputLongitude : post.getInputLongitude());
        post.setTargetLocation(this.targetLocation != null ? this.targetLocation : post.getTargetLocation());
        post.setTargetLatitude(this.targetLatitude != 0 ? this.targetLatitude : post.getTargetLatitude());
        post.setTargetLongitude(this.targetLongitude != 0 ? this.targetLongitude : post.getTargetLongitude());
        post.setUpdatedAt(LocalDateTime.now());
        return post;
    }

}
