package com.github.accountmanagementproject.web.dto.runJoinPost.crew;


import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.repository.runningPost.enums.PostType;
import com.github.accountmanagementproject.repository.runningPost.RunJoinPost;
import com.github.accountmanagementproject.repository.runningPost.enums.RunJoinPostStatus;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrewRunPostCreateRequest {

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


    public static RunJoinPost toEntity(CrewRunPostCreateRequest request, MyUser user, Crew crew) {
        RunJoinPost post = RunJoinPost.builder()
                .author(user)
                .crew(crew)
                .title(request.getTitle())
                .content(request.getContent())
                .maxParticipants(request.getMaxParticipants())
                .inputLocation(request.getInputLocation())
                .inputLatitude(request.getInputLatitude())
                .inputLongitude(request.getInputLongitude())
                .targetLocation(request.getTargetLocation())
                .targetLatitude(request.getTargetLatitude())
                .targetLongitude(request.getTargetLongitude())
                .status(RunJoinPostStatus.OPEN)
                .postType(PostType.CREW)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build();

        return post;
    }
}
