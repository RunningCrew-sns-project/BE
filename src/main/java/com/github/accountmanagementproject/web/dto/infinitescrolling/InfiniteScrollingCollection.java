package com.github.accountmanagementproject.web.dto.infinitescrolling;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InfiniteScrollingCollection<T extends ScrollingResponseInterface>  {
    private final List<T> itemsWithNextCursor;
    private final int countPerScroll;

    public static <T extends ScrollingResponseInterface> InfiniteScrollingCollection<T> of(List<T> itemsWithNextCursor, int size) {
        return new InfiniteScrollingCollection<>(itemsWithNextCursor, size);
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

    public T getNextCursor() {
        return isLastScroll() ? null:
                itemsWithNextCursor.get(countPerScroll);
    }
    public Long getNextCursorId() {
        return isLastScroll() ? null : itemsWithNextCursor.get(countPerScroll).getId();
    }
}
