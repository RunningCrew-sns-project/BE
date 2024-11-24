package com.github.accountmanagementproject.repository.runningPost.enums;

public enum CrewRunJoinPostStatus implements BaseEnum {
    OPEN("모집중"),
    FULL("만원"),
    END("종료");

    private final String value;

    CrewRunJoinPostStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public static GeneralRunJoinPostStatus fromValue(String value) {
        return BaseEnum.fromValue(GeneralRunJoinPostStatus.class, value);
    }
}
