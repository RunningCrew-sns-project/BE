package com.github.accountmanagementproject.repository.runningPost.enums;


public enum ParticipationStatus implements BaseEnum {

    PENDING("참여 대기"),
    APPROVED("참여 확정"),
    REJECTED("참여 거절"),
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
