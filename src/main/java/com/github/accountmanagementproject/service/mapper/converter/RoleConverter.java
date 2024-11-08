package com.github.accountmanagementproject.service.mapper.converter;

import com.github.accountmanagementproject.repository.account.user.myenum.RolesEnum;

public class RoleConverter extends MyConverter<RolesEnum> {

    public RoleConverter() {
        super( RolesEnum.class);
    }
}
