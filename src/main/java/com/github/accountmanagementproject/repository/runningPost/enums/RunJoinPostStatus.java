package com.github.accountmanagementproject.repository.runningPost.enums;


public enum RunJoinPostStatus implements BaseEnum {
    OPEN("모집중"),    // 모집 중
    ONGOING("진행중"),
    CLOSED("완료");  // 모집 마감

    private final String value;

    RunJoinPostStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    // 문자열을 enum으로 변환하는 정적 메서드
    public static RunJoinPostStatus fromValue(String value) {
        return BaseEnum.fromValue(RunJoinPostStatus.class, value);
    }

}
