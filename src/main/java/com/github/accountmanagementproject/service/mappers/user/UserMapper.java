package com.github.accountmanagementproject.service.mappers.user;

import com.github.accountmanagementproject.config.client.dto.userInfo.OAuthUserInfo;
import com.github.accountmanagementproject.repository.account.socialIds.SocialId;
import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.account.users.enums.RolesEnum;
import com.github.accountmanagementproject.repository.account.users.roles.Role;
import com.github.accountmanagementproject.web.dto.accountAuth.myPage.AccountInfoDto;
import com.github.accountmanagementproject.web.dto.accountAuth.myPage.AccountInfoResponse;
import com.github.accountmanagementproject.web.dto.accountAuth.myPage.AccountMain;
import com.github.accountmanagementproject.web.dto.accountAuth.oauth.request.OAuthSignUpRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;


@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "roles", qualifiedByName = "getMyRoles")
    @Mapping(target = "gender", source = "myUser.gender")
    @Mapping(target = "dateOfBirth", dateFormat = "yyyy년 M월 d일")
    @Mapping(target = "lastLogin", dateFormat = "yyyy년 M월 d일 HH:mm:ss")
    AccountInfoResponse myUserToAccountDto(MyUser myUser);

    @Mapping(target = "dateOfBirth", dateFormat = "yyyy-M-d")
    @Mapping(target = "roles", ignore = true)
    MyUser accountDtoToMyUser(AccountInfoDto accountInfoDto);

    @Named("getMyRoles")
    default Set<RolesEnum> myRoles(Set<Role> roles){
        return roles.stream().map(r->r.getName())
                .collect(Collectors.toSet());
    }

////닉네임이 고유값이어야 할때    @Mapping(target = "nickname", expression = "java(oAuthInfoResponse.getNickName()+\"_\"+oAuthInfoResponse.getSocialId())")
    @Mapping(target = "password", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "status", constant = "TEMP")
    MyUser oAuthInfoResponseToMyUser(OAuthUserInfo oAuthUserInfo);

    @AfterMapping
    default void assignSocialId(OAuthUserInfo oAuthUserInfo, @MappingTarget MyUser.MyUserBuilder myUserBuilder) {
        myUserBuilder.socialIds(Set.of(new SocialId(oAuthUserInfo.getSocialId(), oAuthUserInfo.getOAuthProvider())));
    }
    @Mapping(target = "provider", source = "OAuthProvider")
    OAuthSignUpRequest oAuthUserInfoToOAuthSignUpDto(OAuthUserInfo oAuthUserInfo);


    AccountMain myUserToAccountMain(MyUser myUser);
}
