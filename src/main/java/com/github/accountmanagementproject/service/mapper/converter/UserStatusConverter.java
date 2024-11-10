package com.github.accountmanagementproject.service.mapper.converter;

import com.github.accountmanagementproject.repository.account.user.myenum.UserStatus;

public class UserStatusConverter extends MyConverter<UserStatus> {
    public static final Class<UserStatus> ENUM_CLASS = UserStatus.class;
    public UserStatusConverter() {
        super(ENUM_CLASS);
    }
}
