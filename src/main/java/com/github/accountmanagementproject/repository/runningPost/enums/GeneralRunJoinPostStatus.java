package com.github.accountmanagementproject.repository.runningPost.enums;

public enum GeneralRunJoinPostStatus implements BaseEnum {

    OPEN("모집중"),    // 모집 중
    ONGOING("진행중"),
    CLOSED("완료"),  // 모집 마감
    END("종료"); // 모든 유저의 달리기 종료

    private final String value;

    GeneralRunJoinPostStatus(String value) {
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
