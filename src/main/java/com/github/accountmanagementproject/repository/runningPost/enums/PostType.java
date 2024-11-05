package com.github.accountmanagementproject.repository.runningPost.enums;

public enum PostType implements BaseEnum {
    CREW("크루"),    // 크루 달리기
    GENERAL("일반");  //  일반 달리기

    private final String value;

    PostType(String value) {
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
