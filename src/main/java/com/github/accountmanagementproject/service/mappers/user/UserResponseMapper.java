package com.github.accountmanagementproject.service.mappers.user;

import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.web.dto.chat.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserResponseMapper {
    UserResponseMapper INSTANCE = Mappers.getMapper(UserResponseMapper.class);

    @Mapping(target = "userName", source = "email")
    UserResponse myUserToUserResponse(MyUser myUser);
}
