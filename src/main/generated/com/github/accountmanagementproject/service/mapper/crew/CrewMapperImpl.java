package com.github.accountmanagementproject.service.mapper.crew;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.myenum.Gender;
import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.repository.crew.crewattachment.CrewAttachment;
import com.github.accountmanagementproject.repository.crew.crewimage.CrewImage;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsers;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersPk;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersStatus;
import com.github.accountmanagementproject.web.dto.crew.CrewCreationRequest;
import com.github.accountmanagementproject.web.dto.crew.CrewDetailResponse;
import com.github.accountmanagementproject.web.dto.crew.CrewJoinResponse;
import com.github.accountmanagementproject.web.dto.crew.CrewListResponse;
import com.github.accountmanagementproject.web.dto.crew.CrewUserResponse;
import com.github.accountmanagementproject.web.dto.crew.MyCrewResponse;
import com.github.accountmanagementproject.web.dto.storage.FileDto;
import com.github.accountmanagementproject.web.dto.storage.UrlDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-10T18:03:13+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (JetBrains s.r.o.)"
)
public class CrewMapperImpl implements CrewMapper {

    @Override
    public MyCrewResponse crewsUsersToMyCrewResponse(CrewsUsers crew) {
        if ( crew == null ) {
            return null;
        }

        MyCrewResponse myCrewResponse = new MyCrewResponse();

        Long crewId = crewCrewsUsersPkCrewCrewId( crew );
        if ( crewId != null ) {
            myCrewResponse.setCrewId( crewId );
        }
        myCrewResponse.setCrewName( crewCrewsUsersPkCrewCrewName( crew ) );
        myCrewResponse.setCrewIntroduction( crewCrewsUsersPkCrewCrewIntroduction( crew ) );
        myCrewResponse.setStatus( crew.getStatus() );

        myCrewResponse.setCrewMaster( crew.getStatus() == null ? true : false );
        myCrewResponse.setCrewImageUrl( getFirstCrewImageUrl(crew.getCrewsUsersPk().getCrew().getCrewImages()) );
        myCrewResponse.setRequestOrCompletionDate( isStatusCompleted(crew) ? crew.getJoinDate() : (crew.getStatus() == null ? crew.getCrewsUsersPk().getCrew().getCreatedAt() : crew.getApplicationDate()) );

        return myCrewResponse;
    }

    @Override
    public Crew crewCreationRequestToCrew(CrewCreationRequest creationRequest, MyUser crewMaster) {
        if ( creationRequest == null && crewMaster == null ) {
            return null;
        }

        Crew crew = new Crew();

        if ( creationRequest != null ) {
            crew.setCrewImages( urlDtoListToCrewImageList( creationRequest.getCrewImageUrls() ) );
            crew.setCrewAttachments( fileDtoListToCrewAttachmentList( creationRequest.getFileDtos() ) );
            crew.setCrewName( creationRequest.getCrewName() );
            crew.setCrewIntroduction( creationRequest.getCrewIntroduction() );
            crew.setActivityRegion( creationRequest.getActivityRegion() );
            crew.setMaxCapacity( creationRequest.getMaxCapacity() );
        }
        crew.setCrewMaster( crewMaster );

        insertCrewIntoCrewImageAndCrewAttachment( crew );

        return crew;
    }

    @Override
    public CrewImage crewImageCreationRequestToCrewImage(UrlDto imageUrl) {
        if ( imageUrl == null ) {
            return null;
        }

        CrewImage crewImage = new CrewImage();

        crewImage.setImageUrl( imageUrl.getUrl() );

        return crewImage;
    }

    @Override
    public CrewAttachment fileDtoToCrewAttachment(FileDto fileDto) {
        if ( fileDto == null ) {
            return null;
        }

        CrewAttachment crewAttachment = new CrewAttachment();

        crewAttachment.setFileName( fileDto.getFileName() );
        crewAttachment.setFileUrl( fileDto.getFileUrl() );

        return crewAttachment;
    }

    @Override
    public CrewJoinResponse crewsUsersToCrewJoinResponse(CrewsUsers joinCrew) {
        if ( joinCrew == null ) {
            return null;
        }

        String crewName = null;
        LocalDateTime applicationDate = null;
        CrewsUsersStatus status = null;

        crewName = crewCrewsUsersPkCrewCrewName( joinCrew );
        applicationDate = joinCrew.getApplicationDate();
        status = joinCrew.getStatus();

        CrewJoinResponse crewJoinResponse = new CrewJoinResponse( crewName, status, applicationDate );

        crewJoinResponse.setJoinCompleted( isStatusCompleted(joinCrew) );

        return crewJoinResponse;
    }

    @Override
    public CrewUserResponse crewsUsersToCrewUserResponse(CrewsUsers crewsUsers) {
        if ( crewsUsers == null ) {
            return null;
        }

        CrewUserResponse crewUserResponse = new CrewUserResponse();

        crewUserResponse.setEmail( crewsUsersCrewsUsersPkUserEmail( crewsUsers ) );
        crewUserResponse.setNickname( crewsUsersCrewsUsersPkUserNickname( crewsUsers ) );
        crewUserResponse.setUserImageUrl( crewsUsersCrewsUsersPkUserProfileImg( crewsUsers ) );
        crewUserResponse.setProfileMessage( crewsUsersCrewsUsersPkUserProfileMessage( crewsUsers ) );
        crewUserResponse.setGender( crewsUsersCrewsUsersPkUserGender( crewsUsers ) );
        crewUserResponse.setLastLoginDate( crewsUsersCrewsUsersPkUserLastLogin( crewsUsers ) );
        crewUserResponse.setStatus( crewsUsers.getStatus() );
        if ( crewsUsers.getCaveat() != null ) {
            crewUserResponse.setCaveat( crewsUsers.getCaveat() );
        }

        crewUserResponse.setJoinRequestOrJoinDate( isStatusCompleted(crewsUsers) ? crewsUsers.getJoinDate() : crewsUsers.getApplicationDate() );

        return crewUserResponse;
    }

    @Override
    public CrewDetailResponse crewToCrewDetailResponse(Crew crew) {
        if ( crew == null ) {
            return null;
        }

        String crewMaster = null;
        List<String> crewImageUrls = null;
        String crewName = null;
        String crewIntroduction = null;
        String activityRegion = null;
        LocalDateTime createdAt = null;
        int maxCapacity = 0;

        crewMaster = crewCrewMasterNickname( crew );
        crewImageUrls = mapCrewImagesToUrls( crew.getCrewImages() );
        crewName = crew.getCrewName();
        crewIntroduction = crew.getCrewIntroduction();
        activityRegion = crew.getActivityRegion();
        createdAt = crew.getCreatedAt();
        maxCapacity = crew.getMaxCapacity();

        long memberCount = 0L;

        CrewDetailResponse crewDetailResponse = new CrewDetailResponse( crewName, crewIntroduction, crewImageUrls, crewMaster, activityRegion, createdAt, memberCount, maxCapacity );

        return crewDetailResponse;
    }

    @Override
    public List<String> mapCrewImagesToUrls(List<CrewImage> crewImages) {
        if ( crewImages == null ) {
            return null;
        }

        List<String> list = new ArrayList<String>( crewImages.size() );
        for ( CrewImage crewImage : crewImages ) {
            list.add( crewImageToImageUrl( crewImage ) );
        }

        return list;
    }

    @Override
    public List<CrewListResponse> crewsToCrewListResponses(List<Crew> crews) {
        if ( crews == null ) {
            return null;
        }

        List<CrewListResponse> list = new ArrayList<CrewListResponse>( crews.size() );
        for ( Crew crew : crews ) {
            list.add( crewToCrewListResponse( crew ) );
        }

        return list;
    }

    @Override
    public CrewListResponse crewToCrewListResponse(Crew crew) {
        if ( crew == null ) {
            return null;
        }

        long crewId = 0L;
        String crewName = null;
        String crewIntroduction = null;
        String activityRegion = null;
        LocalDateTime createdAt = null;
        int maxCapacity = 0;

        if ( crew.getCrewId() != null ) {
            crewId = crew.getCrewId();
        }
        crewName = crew.getCrewName();
        crewIntroduction = crew.getCrewIntroduction();
        activityRegion = crew.getActivityRegion();
        createdAt = crew.getCreatedAt();
        maxCapacity = crew.getMaxCapacity();

        String crewImageUrl = getFirstCrewImageUrl(crew.getCrewImages());
        long memberCount = 0L;
        Long popularNumerical = null;

        CrewListResponse crewListResponse = new CrewListResponse( crewId, crewName, crewImageUrl, crewIntroduction, activityRegion, createdAt, memberCount, maxCapacity, popularNumerical );

        return crewListResponse;
    }

    private Long crewCrewsUsersPkCrewCrewId(CrewsUsers crewsUsers) {
        if ( crewsUsers == null ) {
            return null;
        }
        CrewsUsersPk crewsUsersPk = crewsUsers.getCrewsUsersPk();
        if ( crewsUsersPk == null ) {
            return null;
        }
        Crew crew = crewsUsersPk.getCrew();
        if ( crew == null ) {
            return null;
        }
        Long crewId = crew.getCrewId();
        if ( crewId == null ) {
            return null;
        }
        return crewId;
    }

    private String crewCrewsUsersPkCrewCrewName(CrewsUsers crewsUsers) {
        if ( crewsUsers == null ) {
            return null;
        }
        CrewsUsersPk crewsUsersPk = crewsUsers.getCrewsUsersPk();
        if ( crewsUsersPk == null ) {
            return null;
        }
        Crew crew = crewsUsersPk.getCrew();
        if ( crew == null ) {
            return null;
        }
        String crewName = crew.getCrewName();
        if ( crewName == null ) {
            return null;
        }
        return crewName;
    }

    private String crewCrewsUsersPkCrewCrewIntroduction(CrewsUsers crewsUsers) {
        if ( crewsUsers == null ) {
            return null;
        }
        CrewsUsersPk crewsUsersPk = crewsUsers.getCrewsUsersPk();
        if ( crewsUsersPk == null ) {
            return null;
        }
        Crew crew = crewsUsersPk.getCrew();
        if ( crew == null ) {
            return null;
        }
        String crewIntroduction = crew.getCrewIntroduction();
        if ( crewIntroduction == null ) {
            return null;
        }
        return crewIntroduction;
    }

    protected List<CrewImage> urlDtoListToCrewImageList(List<UrlDto> list) {
        if ( list == null ) {
            return null;
        }

        List<CrewImage> list1 = new ArrayList<CrewImage>( list.size() );
        for ( UrlDto urlDto : list ) {
            list1.add( crewImageCreationRequestToCrewImage( urlDto ) );
        }

        return list1;
    }

    protected List<CrewAttachment> fileDtoListToCrewAttachmentList(List<FileDto> list) {
        if ( list == null ) {
            return null;
        }

        List<CrewAttachment> list1 = new ArrayList<CrewAttachment>( list.size() );
        for ( FileDto fileDto : list ) {
            list1.add( fileDtoToCrewAttachment( fileDto ) );
        }

        return list1;
    }

    private String crewsUsersCrewsUsersPkUserEmail(CrewsUsers crewsUsers) {
        if ( crewsUsers == null ) {
            return null;
        }
        CrewsUsersPk crewsUsersPk = crewsUsers.getCrewsUsersPk();
        if ( crewsUsersPk == null ) {
            return null;
        }
        MyUser user = crewsUsersPk.getUser();
        if ( user == null ) {
            return null;
        }
        String email = user.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }

    private String crewsUsersCrewsUsersPkUserNickname(CrewsUsers crewsUsers) {
        if ( crewsUsers == null ) {
            return null;
        }
        CrewsUsersPk crewsUsersPk = crewsUsers.getCrewsUsersPk();
        if ( crewsUsersPk == null ) {
            return null;
        }
        MyUser user = crewsUsersPk.getUser();
        if ( user == null ) {
            return null;
        }
        String nickname = user.getNickname();
        if ( nickname == null ) {
            return null;
        }
        return nickname;
    }

    private String crewsUsersCrewsUsersPkUserProfileImg(CrewsUsers crewsUsers) {
        if ( crewsUsers == null ) {
            return null;
        }
        CrewsUsersPk crewsUsersPk = crewsUsers.getCrewsUsersPk();
        if ( crewsUsersPk == null ) {
            return null;
        }
        MyUser user = crewsUsersPk.getUser();
        if ( user == null ) {
            return null;
        }
        String profileImg = user.getProfileImg();
        if ( profileImg == null ) {
            return null;
        }
        return profileImg;
    }

    private String crewsUsersCrewsUsersPkUserProfileMessage(CrewsUsers crewsUsers) {
        if ( crewsUsers == null ) {
            return null;
        }
        CrewsUsersPk crewsUsersPk = crewsUsers.getCrewsUsersPk();
        if ( crewsUsersPk == null ) {
            return null;
        }
        MyUser user = crewsUsersPk.getUser();
        if ( user == null ) {
            return null;
        }
        String profileMessage = user.getProfileMessage();
        if ( profileMessage == null ) {
            return null;
        }
        return profileMessage;
    }

    private Gender crewsUsersCrewsUsersPkUserGender(CrewsUsers crewsUsers) {
        if ( crewsUsers == null ) {
            return null;
        }
        CrewsUsersPk crewsUsersPk = crewsUsers.getCrewsUsersPk();
        if ( crewsUsersPk == null ) {
            return null;
        }
        MyUser user = crewsUsersPk.getUser();
        if ( user == null ) {
            return null;
        }
        Gender gender = user.getGender();
        if ( gender == null ) {
            return null;
        }
        return gender;
    }

    private LocalDateTime crewsUsersCrewsUsersPkUserLastLogin(CrewsUsers crewsUsers) {
        if ( crewsUsers == null ) {
            return null;
        }
        CrewsUsersPk crewsUsersPk = crewsUsers.getCrewsUsersPk();
        if ( crewsUsersPk == null ) {
            return null;
        }
        MyUser user = crewsUsersPk.getUser();
        if ( user == null ) {
            return null;
        }
        LocalDateTime lastLogin = user.getLastLogin();
        if ( lastLogin == null ) {
            return null;
        }
        return lastLogin;
    }

    private String crewCrewMasterNickname(Crew crew) {
        if ( crew == null ) {
            return null;
        }
        MyUser crewMaster = crew.getCrewMaster();
        if ( crewMaster == null ) {
            return null;
        }
        String nickname = crewMaster.getNickname();
        if ( nickname == null ) {
            return null;
        }
        return nickname;
    }
}
