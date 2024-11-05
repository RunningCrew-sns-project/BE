package com.github.accountmanagementproject.service.mapper.crew;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.repository.crew.crewattachment.CrewAttachment;
import com.github.accountmanagementproject.repository.crew.crewimage.CrewImage;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsers;
import com.github.accountmanagementproject.web.dto.crew.CrewCreationRequest;
import com.github.accountmanagementproject.web.dto.crew.CrewJoinResponse;
import com.github.accountmanagementproject.web.dto.crew.CrewUserResponse;
import com.github.accountmanagementproject.web.dto.crew.MyCrewResponse;
import com.github.accountmanagementproject.web.dto.storage.FileDto;
import com.github.accountmanagementproject.web.dto.storage.UrlDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CrewMapper {
    CrewMapper INSTANCE = Mappers.getMapper(CrewMapper.class);


    @Mapping(target = "crewId", source = "crew.crewsUsersPk.crew.crewId")
    @Mapping(target = "crewName", source = "crew.crewsUsersPk.crew.crewName")
    @Mapping(target = "crewIntroduction", source = "crew.crewsUsersPk.crew.crewIntroduction")
    @Mapping(target = "crewMaster", expression = "java(crew.getStatus() == null ? true : false)")
    @Mapping(target = "crewImageUrl", expression = "java(crew.getCrewsUsersPk().getCrew().getCrewImages().isEmpty() ? null : crew.getCrewsUsersPk().getCrew().getCrewImages().get(0).getImageUrl())")
    @Mapping(target = "requestOrCompletionDate",
            expression = "java(crew.getStatus() == " +
                    "com.github.runningcrewsnsproject.repository.crew.crewuser.CrewsUsersStatus.COMPLETED ?" +
                    " crew.getJoinDate() " +
                    ": (crew.getStatus() == null ? crew.getCrewsUsersPk().getCrew().getCreatedAt() : crew.getApplicationDate()))")
    @Mapping(target = "status", source = "crew.status")
    MyCrewResponse crewsUsersToMyCrewResponse(CrewsUsers crew);





    default List<MyCrewResponse> crewsUsersListToMyCrewResponseList(List<CrewsUsers> crewsUsers) {
        return crewsUsers.stream().map(this::crewsUsersToMyCrewResponse).toList();
    }



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
    @Mapping(target = "joinCompleted", expression = "java(joinCrew.getStatus() == com.github.runningcrewsnsproject.repository.crew.crewuser.CrewsUsersStatus.COMPLETED)")
    CrewJoinResponse crewsUsersToCrewJoinResponse(CrewsUsers joinCrew);

    @Mapping(target = "email", source = "crewsUsersPk.user.email")
    @Mapping(target = "nickname", source = "crewsUsersPk.user.nickname")
    @Mapping(target = "userImageUrl", source = "crewsUsersPk.user.profileImg")
    @Mapping(target = "profileMessage", source = "crewsUsersPk.user.profileMessage")
    @Mapping(target = "gender", source = "crewsUsersPk.user.gender")
    @Mapping(target = "lastLoginDate", source = "crewsUsersPk.user.lastLogin")
    @Mapping(target = "joinRequestOrJoinDate", expression =
            "java(crewsUsers.getStatus() == com.github.runningcrewsnsproject.repository.crew.crewuser.CrewsUsersStatus.COMPLETED ? " +
                    "crewsUsers.getJoinDate() : crewsUsers.getApplicationDate())")
    CrewUserResponse crewsUsersToCrewUserResponse(CrewsUsers crewsUsers);
}
