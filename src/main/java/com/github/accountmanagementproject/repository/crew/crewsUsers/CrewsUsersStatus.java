package com.github.accountmanagementproject.repository.crew.crewsUsers;

import com.fasterxml.jackson.annotation.JsonValue;
import com.github.accountmanagementproject.repository.account.users.enums.MyEnumInterface;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CrewsUsersStatus implements MyEnumInterface {
    COMPLETED("가입 완료"),
    WAITING("가입 대기");


    private final String value;


    @Override
    @JsonValue
    public String getValue() {
        return this.value;
    }

}
