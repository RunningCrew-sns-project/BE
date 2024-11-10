package com.github.accountmanagementproject.service.mapper.user;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.web.dto.chat.UserResponse;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-10T18:03:13+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (JetBrains s.r.o.)"
)
public class UserResponseMapperImpl implements UserResponseMapper {

    @Override
    public UserResponse myUserToUserResponse(MyUser myUser) {
        if ( myUser == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.userName( myUser.getEmail() );
        if ( myUser.getUserId() != null ) {
            userResponse.userId( myUser.getUserId().intValue() );
        }

        return userResponse.build();
    }
}
