package com.github.accountmanagementproject.alarm.repository;


import com.github.accountmanagementproject.repository.runningPost.enums.BaseEnum;

public enum NotificationType implements BaseEnum {
//    NEW_JOIN_REQUEST("크루 가입 신청"),
    JOIN_REQUEST_APPROVED("가입 완료"),
    JOIN_REQUEST_REJECTED("가입 거절");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    // 문자열을 enum으로 변환
    public static NotificationType fromValue(String value) {
        return BaseEnum.fromValue(NotificationType.class, value);
    }
}
