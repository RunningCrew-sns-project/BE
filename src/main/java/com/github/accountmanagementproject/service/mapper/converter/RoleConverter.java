package com.github.accountmanagementproject.service.mapper.converter;

import com.github.accountmanagementproject.repository.account.user.myenum.RolesEnum;

public class RoleConverter extends MyConverter<RolesEnum> {
    public static final Class<RolesEnum> ENUM_CLASS = RolesEnum.class;

    public RoleConverter() {
        super(ENUM_CLASS);
    }
}
