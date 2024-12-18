package com.github.accountmanagementproject.web.dto.infinitescrolling;

public interface ScrollingResponseInterface<U extends Enum<U>> {
    String nextCursor(U criteria);
    long nextCursorId();

}