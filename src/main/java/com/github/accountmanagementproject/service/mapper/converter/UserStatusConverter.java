package com.github.accountmanagementproject.service.mapper.converter;

import com.github.accountmanagementproject.repository.account.user.myenum.UserStatus;

public class UserStatusConverter extends MyConverter<UserStatus> {
    public UserStatusConverter() {
        super(UserStatus.class);
    }
}
