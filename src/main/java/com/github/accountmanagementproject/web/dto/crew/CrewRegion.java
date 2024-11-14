package com.github.accountmanagementproject.web.dto.crew;

import com.github.accountmanagementproject.repository.account.user.myenum.MyEnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CrewRegion implements MyEnumInterface {
    SEOUL("서울"),
    BUSAN("부산"),
    DAEGU("대구"),
    INCHEON("인천"),
    GWANGJU("광주"),
    DAEJEON("대전"),
    ULSAN("울산"),
    GYEONGGI("경기도"),
    GANGWON("강원도"),
    CHUNGCHEONG("충청도"),
    JEOLLA("전라도"),
    GYEONGSANG("경상도"),
    JEJU("제주도");

    private final String value;

}
