package com.github.accountmanagementproject.repository.crew.crewuser;

import com.github.accountmanagementproject.repository.account.user.myenum.MyEnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CrewsUsersStatus implements MyEnumInterface {
    COMPLETED("가입 완료"),
    WAITING("가입 대기"),
    WITHDRAWAL("탈퇴"),
    REJECTED("가입 거절"),
    FORCED_EXIT("강제 퇴장");


    private final String value;

}
