package com.github.accountmanagementproject.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

//https://kbwplace.tistory.com/178 No offset 방식 스크롤링 구현
//https://velog.io/@orijoon98/Spring-%EB%AC%B4%ED%95%9C%EC%8A%A4%ED%81%AC%EB%A1%A4-%EA%B5%AC%ED%98%84-1-%EC%BB%A4%EC%84%9C-%EA%B8%B0%EB%B0%98-%ED%8E%98%EC%9D%B4%EC%A7%80%EB%84%A4%EC%9D%B4%EC%85%98
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public class ScrollPaginationCollection<T> {
    private final List<T> currentScrollItems; // 현재 스크롤의 요소들
    private final int countPerScroll; //한번에 불러올 갯수
    private final boolean lastScroll; // 스크롤의 마지막 여부
    private final T nextCursor; // 다음 스크롤의 커서 정보

    public static <T> ScrollPaginationCollection<T> of(List<T> currentScrollItems, int size, boolean lastScroll, T nextCursor) {
        return new ScrollPaginationCollection<>(currentScrollItems, size, lastScroll, nextCursor);
    }
}
