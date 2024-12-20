package com.github.accountmanagementproject.web.dto.runJoinPost.crew;

import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;
import com.github.accountmanagementproject.repository.runningPost.crewRunGroup.CrewRunGroupRepository;
import com.github.accountmanagementproject.repository.runningPost.image.CrewJoinPostImage;
import com.github.accountmanagementproject.repository.runningPost.image.RunJoinPostImage;
import com.github.accountmanagementproject.web.dto.storage.FileDto;
import com.github.accountmanagementproject.web.dto.storage.UrlDto;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Setter
public class CrewRunPostResponseMapper {

    private CrewRunPostResponseMapper() {
        // 인스턴스화 방지
    }

    public static CrewRunPostResponse toDto(CrewJoinPost runJoinPost,  Crew crew) {

        List<CrewJoinPostImage> images = runJoinPost.getCrewJoinPostImages();
        List<UrlDto> urlDtos = new ArrayList<>();

        if (images != null) {
            images.forEach(image -> {
                UrlDto urlDto = new UrlDto();
                urlDto.setUrl(image.getImageUrl());
                urlDtos.add(urlDto);
            });
        }

        // 참여 인원 수 계산
//        int participantCount = runJoinPost.getParticipants() != null ? runJoinPost.getParticipants().size() : 0;
        int participantCount = runJoinPost.getCurrentPeople() != null ? runJoinPost.getCurrentPeople() : 0;

        // Crew 정보
        String crewName = crew != null ? crew.getCrewName() : null;
        String crewDescription = crew != null ? crew.getCrewIntroduction() : null;
        String crewImageUrl = (crew != null && !crew.getCrewImages().isEmpty()) ? crew.getCrewImages().get(0).getImageUrl() : null;

        // CrewRunPostResponse 객체 생성 및 반환
        return CrewRunPostResponse.builder()
                .runId(runJoinPost.getCrewPostId())
                .crewId(crew != null ? crew.getCrewId() : null)
                .authorId(runJoinPost.getAuthor() != null ? runJoinPost.getAuthor().getUserId() : null)
                .title(runJoinPost.getTitle())
                .content(runJoinPost.getContent())
                .maximumPeople(runJoinPost.getMaximumPeople())
                .people(participantCount)  // 현재인원 추가
                .location(runJoinPost.getLocation())
                .status(runJoinPost.getStatus())
                .postType(runJoinPost.getPostType())
                .date(runJoinPost.getDate())  // 날짜 추가
                .startTime(runJoinPost.getStartTime() != null ? runJoinPost.getStartTime() : null)
                .inputLocation(runJoinPost.getInputLocation())
                .inputLatitude(runJoinPost.getInputLatitude())
                .inputLongitude(runJoinPost.getInputLongitude())
                .targetLocation(runJoinPost.getTargetLocation())
                .targetLatitude(runJoinPost.getTargetLatitude())
                .targetLongitude(runJoinPost.getTargetLongitude())
                .distance(runJoinPost.getDistance())
                .createdAt(runJoinPost.getCreatedAt())
                .updatedAt(runJoinPost.getUpdatedAt())
                .crewPostImageUrl(urlDtos)  // 달리기 게시물 이미지
                .crewName(crewName)                // 크루 이름 추가
                .crewDescription(crewDescription)  // 크루 소개 추가
                .crewImageUrl(crewImageUrl)        // 크루 이미지 URL 추가
                .build();
    }
}
