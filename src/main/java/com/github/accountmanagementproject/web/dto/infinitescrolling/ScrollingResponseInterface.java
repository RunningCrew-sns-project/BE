package com.github.accountmanagementproject.web.dto.infinitescrolling;

public interface ScrollingResponseInterface<U> {
    String nextCursor(U criteria);
    long nextCursorId();

}