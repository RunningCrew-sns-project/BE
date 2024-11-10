package com.github.accountmanagementproject.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public class ScrollPaginationCollection<T> {
    private final List<T> currentScrollItems; // 현재 스크롤의 요소들
    private final int countPerScroll;
    private final boolean lastScroll; // 스크롤의 마지막 여부
    private final T nextCursor; // 다음 스크롤의 커서 정보

    public static <T> ScrollPaginationCollection<T> of(List<T> currentScrollItems, int size, boolean lastScroll, T nextCursor) {
        return new ScrollPaginationCollection<>(currentScrollItems, size, lastScroll, nextCursor);
    }
}
