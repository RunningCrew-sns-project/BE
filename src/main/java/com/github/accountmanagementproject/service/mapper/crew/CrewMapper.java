package com.github.accountmanagementproject.service.mapper.crew;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.repository.crew.crewattachment.CrewAttachment;
import com.github.accountmanagementproject.repository.crew.crewimage.CrewImage;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsers;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersStatus;
import com.github.accountmanagementproject.web.dto.crew.*;
import com.github.accountmanagementproject.web.dto.storage.FileDto;
import com.github.accountmanagementproject.web.dto.storage.UrlDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Mapper
public interface CrewMapper {
    CrewMapper INSTANCE = Mappers.getMapper(CrewMapper.class);


    @Mapping(target = "crewId", source = "crew.crewsUsersPk.crew.crewId")
    @Mapping(target = "crewName", source = "crew.crewsUsersPk.crew.crewName")
    @Mapping(target = "crewIntroduction", source = "crew.crewsUsersPk.crew.crewIntroduction")
    @Mapping(target = "crewMaster", expression = "java(crew.getStatus() == null ? true : false)")
    @Mapping(target = "crewImageUrl", expression = "java(getFirstCrewImageUrl(crew.getCrewsUsersPk().getCrew().getCrewImages()))")
    @Mapping(target = "requestOrCompletionDate",
            expression = "java(isStatusCompleted(crew) ?" +
                    " crew.getJoinDate() " +
                    ": (crew.getStatus() == null ? crew.getCrewsUsersPk().getCrew().getCreatedAt() : crew.getApplicationDate()))")
    @Mapping(target = "status", source = "crew.status")
    MyCrewResponse crewsUsersToMyCrewResponse(CrewsUsers crew);

    default boolean isStatusCompleted(CrewsUsers crew) {
        return crew.getStatus() == CrewsUsersStatus.COMPLETED;
    }



//    default List<MyCrewResponse> crewsUsersListToMyCrewResponseList(List<CrewsUsers> crewsUsers) {
//        return crewsUsers.stream().map(this::crewsUsersToMyCrewResponse).toList();
//    }



    @Mapping(target = "crewImages", source = "creationRequest.crewImageUrls")
    @Mapping(target = "crewAttachments", source = "creationRequest.fileDtos")
    @Mapping(target = "crewMaster", source = "crewMaster")
    @Mapping(target = "createdAt", ignore = true)
    Crew crewCreationRequestToCrew(CrewCreationRequest creationRequest, MyUser crewMaster);

    @Mapping(target = "imageUrl", source = "imageUrl.url")
    CrewImage crewImageCreationRequestToCrewImage(UrlDto imageUrl);


    CrewAttachment fileDtoToCrewAttachment(FileDto fileDto);

    @AfterMapping
    default void insertCrewIntoCrewImageAndCrewAttachment(@MappingTarget Crew crew) {
        if(crew.getCrewImages() != null) {
            crew.getCrewImages().forEach(crewImage -> crewImage.setCrew(crew));
        }
        if(crew.getCrewAttachments() != null) {
            crew.getCrewAttachments().forEach(crewAttachment -> crewAttachment.setCrew(crew));
        }
    }
    @Mapping(target = "crewName", source = "joinCrew.crewsUsersPk.crew.crewName")
    @Mapping(target = "applicationDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "joinCompleted", expression = "java(isStatusCompleted(joinCrew))")
    CrewJoinResponse crewsUsersToCrewJoinResponse(CrewsUsers joinCrew);

    @Mapping(target = "email", source = "crewsUsersPk.user.email")
    @Mapping(target = "nickname", source = "crewsUsersPk.user.nickname")
    @Mapping(target = "userImageUrl", source = "crewsUsersPk.user.profileImg")
    @Mapping(target = "profileMessage", source = "crewsUsersPk.user.profileMessage")
    @Mapping(target = "gender", source = "crewsUsersPk.user.gender")
    @Mapping(target = "lastLoginDate", source = "crewsUsersPk.user.lastLogin")
    @Mapping(target = "joinRequestOrJoinDate", expression =
            "java(isStatusCompleted(crewsUsers) ? " +
                    "crewsUsers.getJoinDate() : crewsUsers.getApplicationDate())")
    CrewUserResponse crewsUsersToCrewUserResponse(CrewsUsers crewsUsers);

    @Mapping(target = "crewMaster", source = "crewMaster.nickname")
    @Mapping(target = "crewImageUrls", source = "crewImages")
    CrewDetailResponse crewToCrewDetailResponse(Crew crew);

    @Named("crewImageToImageUrl")
    default String crewImageToImageUrl(CrewImage crewImage){
        return crewImage.getImageUrl();
    };


    @IterableMapping(qualifiedByName = "crewImageToImageUrl")
    List<String> mapCrewImagesToUrls(List<CrewImage> crewImages);



    // Crew 리스트를 CrewListResponse 리스트로 매핑
    @IterableMapping(qualifiedByName = "crewForListResponse")
    List<CrewListResponse> crewListToCrewListResponse(List<Crew> crews);

    @Named("crewForListResponse")
    @Mapping(target = "crewId", source = "crewId")
    @Mapping(target = "crewName", source = "crewName")
    @Mapping(target = "crewIntroduction", source = "crewIntroduction")
//    @Mapping(target = "crewImageUrls", source = "crewImages") // Crew의 이미지 URL 리스트 매핑
//    @Mapping(target = "crewMaster", source = "crewMaster.nickname")  // crewMaster 닉네임
    @Mapping(target = "activityRegion", source = "activityRegion")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "maxCapacity", source = "maxCapacity")
    CrewListResponse crewForListResponse(Crew crew);

    List<CrewListResponse> crewsToCrewListResponses(List<Crew> crews);
    @Mapping(target = "crewImageUrl", expression = "java(getFirstCrewImageUrl(crew.getCrewImages()))")
    CrewListResponse crewToCrewListResponse(Crew crew);

    default String getFirstCrewImageUrl(List<CrewImage> img) {
        return img.isEmpty() ? null : img.get(0).getImageUrl();
    }
}
