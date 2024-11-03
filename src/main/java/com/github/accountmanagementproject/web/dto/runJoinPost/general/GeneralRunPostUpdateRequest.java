package com.github.accountmanagementproject.web.dto.runJoinPost.general;


import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.runningPost.RunJoinPost;
import com.github.accountmanagementproject.web.dto.storage.FileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeneralRunPostUpdateRequest {

//    private Integer authorId; // 작성자 ID

//    @NotBlank(message = "게시물 제목을 입력해주세요.")
    private String title; // 게시물 제목

//    @NotBlank(message = "게시물 내용을 입력해주세요.")
    private String content; // 게시물 내용

//    @NotNull(message = "최대 참여 인원.")
    private Integer maxParticipants; // 최대 참여 인원

    // 위치 정보
    private String inputLocation; // 시작 위치
    private double inputLatitude; // 시작 위도
    private double inputLongitude; // 시작 경도

    private String targetLocation; // 종료 위치
    private double targetLatitude; // 종료 위도
    private double targetLongitude; // 종료 경도

    private List<FileDto> fileDtos;  // 파일 이미지


    public RunJoinPost updateEntity(RunJoinPost post, MyUser user) {
        return post.toBuilder()
                .author(user)
                .title(this.title != null ? this.title : post.getTitle())
                .content(this.content != null ? this.content : post.getContent())
                .maxParticipants(this.maxParticipants != null ? this.maxParticipants : post.getMaxParticipants())
                .inputLocation(this.inputLocation != null ? this.inputLocation : post.getInputLocation())
                .inputLatitude(this.inputLatitude != 0 ? this.inputLatitude : post.getInputLatitude())
                .inputLongitude(this.inputLongitude != 0 ? this.inputLongitude : post.getInputLongitude())
                .targetLocation(this.targetLocation != null ? this.targetLocation : post.getTargetLocation())
                .targetLatitude(this.targetLatitude != 0 ? this.targetLatitude : post.getTargetLatitude())
                .targetLongitude(this.targetLongitude != 0 ? this.targetLongitude : post.getTargetLongitude())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
