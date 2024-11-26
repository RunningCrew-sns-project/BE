package com.github.accountmanagementproject.repository.account.user.myenum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RolesEnum implements MyEnumInterface{
    ROLE_ADMIN("운영자"),
    ROLE_USER("유저");

    private final String value;

}
