package com.github.runningcrewsnsproject.repository.crew.crewuser;

import com.fasterxml.jackson.annotation.JsonValue;
import com.github.runningcrewsnsproject.repository.account.user.myenum.MyEnumInterface;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CrewsUsersStatus implements MyEnumInterface {
    COMPLETED("가입 완료"),
    WAITING("가입 대기"),
    FORCED_EXIT("강제 퇴장");


    private final String value;


    @Override
    @JsonValue
    public String getValue() {
        return this.value;
    }

}
