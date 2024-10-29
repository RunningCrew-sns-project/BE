package com.github.accountmanagementproject.service.mappers.crews;

import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.crew.crewAttachments.CrewAttachment;
import com.github.accountmanagementproject.repository.crew.crewImages.CrewImage;
import com.github.accountmanagementproject.repository.crew.crews.Crew;
import com.github.accountmanagementproject.web.dto.crews.CrewCreationRequest;
import com.github.accountmanagementproject.web.dto.storage.FileDto;
import com.github.accountmanagementproject.web.dto.storage.UrlDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
@Mapper
public interface CrewMapper {
    CrewMapper INSTANCE = Mappers.getMapper(CrewMapper.class);

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

}
