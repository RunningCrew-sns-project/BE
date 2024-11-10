package com.github.accountmanagementproject.web.dto.runJoinPost.crew;

import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.repository.crew.crew.CrewsRepository;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;
import com.github.accountmanagementproject.web.dto.storage.FileDto;

import java.util.ArrayList;
import java.util.List;

public class CrewRunPostResponseMapper {

    private CrewRunPostResponseMapper() {
        // 인스턴스화 방지
    }


    public static CrewRunPostResponse toDto(CrewJoinPost runJoinPost,  Crew crew) {

        // 이미지 정보를 FileDto로 변환
        List<FileDto> fileDtos = runJoinPost.getCrewJoinPostImages() != null ?
                runJoinPost.getCrewJoinPostImages().stream()
                        .map(image -> new FileDto(
                                image.getFileName(),
                                image.getImageUrl()
                        ))
                        .toList()
                : new ArrayList<>();

        // 참여 인원 수 계산
        int participantCount = runJoinPost.getParticipants() != null ? runJoinPost.getParticipants().size() : 0;

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
                .banners(fileDtos)
                .crewName(crewName)                // 크루 이름 추가
                .crewDescription(crewDescription)  // 크루 소개 추가
                .crewImageUrl(crewImageUrl)        // 크루 이미지 URL 추가
                .build();
    }
}