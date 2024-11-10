package com.github.accountmanagementproject.service.mapper.user;

import com.github.accountmanagementproject.config.client.dto.userInfo.OAuthUserInfo;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.myenum.UserStatus;
import com.github.accountmanagementproject.web.dto.account.auth.SignUpRequest;
import com.github.accountmanagementproject.web.dto.account.auth.oauth.request.OAuthSignUpRequest;
import com.github.accountmanagementproject.web.dto.account.mypage.AccountInfoResponse;
import com.github.accountmanagementproject.web.dto.account.mypage.AccountSummary;
import com.github.accountmanagementproject.web.dto.account.mypage.TempInfoModifyForFigma;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-10T18:03:13+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (JetBrains s.r.o.)"
)
public class UserMapperImpl implements UserMapper {

    private final DateTimeFormatter dateTimeFormatter_yyyy년_M월_d일_0705652203 = DateTimeFormatter.ofPattern( "yyyy년 M월 d일" );
    private final DateTimeFormatter dateTimeFormatter_yyyy년_M월_d일_HH_mm_ss_1435265835 = DateTimeFormatter.ofPattern( "yyyy년 M월 d일 HH:mm:ss" );
    private final DateTimeFormatter dateTimeFormatter_yyyy_M_d_0277261865 = DateTimeFormatter.ofPattern( "yyyy-M-d" );

    @Override
    public AccountInfoResponse myUserToAccountInfoResponse(MyUser myUser) {
        if ( myUser == null ) {
            return null;
        }

        AccountInfoResponse accountInfoResponse = new AccountInfoResponse();

        accountInfoResponse.setRoles( myRoles( myUser.getRoles() ) );
        accountInfoResponse.setGender( myUser.getGender() );
        if ( myUser.getDateOfBirth() != null ) {
            accountInfoResponse.setDateOfBirth( dateTimeFormatter_yyyy년_M월_d일_0705652203.format( myUser.getDateOfBirth() ) );
        }
        if ( myUser.getLastLogin() != null ) {
            accountInfoResponse.setLastLogin( dateTimeFormatter_yyyy년_M월_d일_HH_mm_ss_1435265835.format( myUser.getLastLogin() ) );
        }
        accountInfoResponse.setEmail( myUser.getEmail() );
        accountInfoResponse.setNickname( myUser.getNickname() );
        accountInfoResponse.setPhoneNumber( myUser.getPhoneNumber() );
        accountInfoResponse.setProfileImg( myUser.getProfileImg() );
        accountInfoResponse.setStatus( myUser.getStatus() );

        return accountInfoResponse;
    }

    @Override
    public MyUser accountDtoToMyUser(SignUpRequest signUpRequest) {
        if ( signUpRequest == null ) {
            return null;
        }

        MyUser.MyUserBuilder myUser = MyUser.builder();

        if ( signUpRequest.getDateOfBirth() != null ) {
            myUser.dateOfBirth( LocalDate.parse( signUpRequest.getDateOfBirth(), dateTimeFormatter_yyyy_M_d_0277261865 ) );
        }
        myUser.email( signUpRequest.getEmail() );
        myUser.nickname( signUpRequest.getNickname() );
        myUser.phoneNumber( signUpRequest.getPhoneNumber() );
        myUser.password( signUpRequest.getPassword() );
        myUser.gender( signUpRequest.getGender() );
        myUser.profileImg( signUpRequest.getProfileImg() );

        return myUser.build();
    }

    @Override
    public MyUser oAuthInfoResponseToMyUser(OAuthUserInfo oAuthUserInfo) {
        if ( oAuthUserInfo == null ) {
            return null;
        }

        MyUser.MyUserBuilder myUser = MyUser.builder();

        myUser.email( oAuthUserInfo.getEmail() );
        myUser.nickname( oAuthUserInfo.getNickname() );
        myUser.profileImg( oAuthUserInfo.getProfileImg() );

        myUser.password( java.util.UUID.randomUUID().toString() );
        myUser.status( UserStatus.TEMP );

        assignSocialId( oAuthUserInfo, myUser );

        return myUser.build();
    }

    @Override
    public OAuthSignUpRequest oAuthUserInfoToOAuthSignUpDto(OAuthUserInfo oAuthUserInfo) {
        if ( oAuthUserInfo == null ) {
            return null;
        }

        OAuthSignUpRequest oAuthSignUpRequest = new OAuthSignUpRequest();

        oAuthSignUpRequest.setProvider( oAuthUserInfo.getOAuthProvider() );
        oAuthSignUpRequest.setEmail( oAuthUserInfo.getEmail() );
        oAuthSignUpRequest.setNickname( oAuthUserInfo.getNickname() );
        oAuthSignUpRequest.setProfileImg( oAuthUserInfo.getProfileImg() );
        oAuthSignUpRequest.setSocialId( oAuthUserInfo.getSocialId() );

        return oAuthSignUpRequest;
    }

    @Override
    public AccountSummary myUserToAccountSummary(MyUser myUser) {
        if ( myUser == null ) {
            return null;
        }

        AccountSummary accountSummary = new AccountSummary();

        accountSummary.setNickname( myUser.getNickname() );
        accountSummary.setProfileImg( myUser.getProfileImg() );
        accountSummary.setProfileMessage( myUser.getProfileMessage() );

        return accountSummary;
    }

    @Override
    public TempInfoModifyForFigma myUserToTempInfoModifyForFigma(MyUser user) {
        if ( user == null ) {
            return null;
        }

        TempInfoModifyForFigma tempInfoModifyForFigma = new TempInfoModifyForFigma();

        tempInfoModifyForFigma.setNickname( user.getNickname() );
        tempInfoModifyForFigma.setProfileImg( user.getProfileImg() );
        tempInfoModifyForFigma.setPhoneNumber( user.getPhoneNumber() );
        tempInfoModifyForFigma.setEmail( user.getEmail() );

        return tempInfoModifyForFigma;
    }
}
