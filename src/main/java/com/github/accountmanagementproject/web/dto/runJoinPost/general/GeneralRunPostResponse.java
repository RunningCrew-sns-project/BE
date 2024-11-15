package com.github.accountmanagementproject.web.dto.runJoinPost.general;

import com.github.accountmanagementproject.repository.runningPost.enums.GeneralRunJoinPostStatus;
import com.github.accountmanagementproject.repository.runningPost.enums.PostType;
import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPost;
import com.github.accountmanagementproject.repository.runningPost.image.RunJoinPostImage;
import com.github.accountmanagementproject.web.dto.storage.FileDto;
import com.github.accountmanagementproject.web.dto.storage.UrlDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
public class GeneralRunPostResponse {

    private Long runId;
    private Integer authorId;           // 작성자 ID
    private String title;
    private String content;
    private Integer maximumPeople;      // 최대인원
    private Integer people;             // 현재인원
    private String location;

    private LocalDate date;             // 모임 날짜
    private LocalTime startTime;        // 모임 시작 시간, 없으면 null

    private GeneralRunJoinPostStatus status;   // 게시글 상태
    private PostType postType;          // 게시자 분류

    // 시작 위치 정보
    private String inputLocation;
    private double inputLatitude;
    private double inputLongitude;

    // 종료 위치 정보
    private String targetLocation;
    private double targetLatitude;
    private double targetLongitude;

    private double distance;             // 총 거리
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

//    private List<UrlDto> imageUrls;
    private List<FileDto> banners;  // 파일 이미지


    public static GeneralRunPostResponse toDto(GeneralJoinPost runJoinPost) {

        List<RunJoinPostImage> images = runJoinPost.getGeneralJoinPostImages();
        List<FileDto> fileDtos = new ArrayList<>();
        List<UrlDto> urlDtos = new ArrayList<>();

        if (images != null) {
            images.forEach(image -> {
                if (image.getFileName() != null) {
                    // 파일로 업로드된 이미지
                    fileDtos.add(new FileDto(image.getFileName(), image.getImageUrl()));
                } else {
                    // URL로 입력된 이미지
                    UrlDto urlDto = new UrlDto();
                    urlDto.setUrl(image.getImageUrl());
                    urlDtos.add(urlDto);
                }
            });
        }

        return GeneralRunPostResponse.builder()
                .runId(runJoinPost.getGeneralPostId())
                .authorId(Math.toIntExact(runJoinPost.getAuthor().getUserId()))
                .title(runJoinPost.getTitle())
                .content(runJoinPost.getContent())
                .maximumPeople(runJoinPost.getMaximumPeople())
                .people(runJoinPost.getCurrentPeople()) // 현재 참여 인원 추가
                .location(runJoinPost.getLocation())
                .date(runJoinPost.getDate())
                .startTime(runJoinPost.getStartTime())
                .status(runJoinPost.getStatus())
                .postType(runJoinPost.getPostType())
                .inputLocation(runJoinPost.getInputLocation())
                .inputLatitude(runJoinPost.getInputLatitude())
                .inputLongitude(runJoinPost.getInputLongitude())
                .targetLocation(runJoinPost.getTargetLocation())
                .targetLatitude(runJoinPost.getTargetLatitude())
                .targetLongitude(runJoinPost.getTargetLongitude())
                .distance(runJoinPost.getDistance())
                .createdAt(runJoinPost.getCreatedAt())
                .updatedAt(runJoinPost.getUpdatedAt())
//                .imageUrls(urlDtos)
                .banners(fileDtos)  // 변환된 이미지 정보 추가
                .build();
    }

    // 현재 참여 인원을 설정하는 메서드 추가
    public void setParticipantCount(int count) {
        this.people = count;
    }
}
