package com.github.accountmanagementproject.repository.runningPost.enums;

public enum CrewRunJoinPostStatus implements BaseEnum {
    OPEN("모집중"),
    FULL("만원");

    private final String value;

    CrewRunJoinPostStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public static RunJoinPostStatus fromValue(String value) {
        return BaseEnum.fromValue(RunJoinPostStatus.class, value);
    }
}
