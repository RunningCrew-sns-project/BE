package com.github.accountmanagementproject.repository.runningPost.userRunGroups;

import com.github.accountmanagementproject.repository.runningPost.enums.BaseEnum;


public enum ParticipationStatus implements BaseEnum  {

    PENDING("가입 대기"),
    APPROVED("가입 완료"),
    REJECTED("가입 거절"),
    FORCED_EXIT("강제 퇴장");    // 강퇴 상태 추가

    private final String value;

    ParticipationStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public static ParticipationStatus fromValue(String value) {
        return BaseEnum.fromValue(ParticipationStatus.class, value);
    }
}
