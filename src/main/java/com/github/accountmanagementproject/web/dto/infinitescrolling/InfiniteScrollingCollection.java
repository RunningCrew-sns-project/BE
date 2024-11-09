package com.github.accountmanagementproject.web.dto.infinitescrolling;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InfiniteScrollingCollection<T extends ScrollingResponseInterface<U>, U>  {
    private final List<T> itemsWithNextCursor;
    private final int countPerScroll;
    private final U criteria;

    public static <T extends ScrollingResponseInterface<U>, U> InfiniteScrollingCollection<T, U> of(List<T> itemsWithNextCursor, int size, U criteria) {
        return new InfiniteScrollingCollection<>(itemsWithNextCursor, size, criteria);
    }

    public boolean isLastScroll() {
        return this.itemsWithNextCursor.size() <= countPerScroll;
    }

    public List<T> getCurrentScrollItems() {
        if (isLastScroll()) {
            return this.itemsWithNextCursor;
        }
        return this.itemsWithNextCursor.subList(0, countPerScroll);
    }

    public String getNextCursor() {
        return isLastScroll() ? null : itemsWithNextCursor.get(countPerScroll).nextCursor(criteria);
    }
}
