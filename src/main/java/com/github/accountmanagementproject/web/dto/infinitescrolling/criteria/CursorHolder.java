package com.github.accountmanagementproject.web.dto.infinitescrolling.criteria;

import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class CursorHolder {
    private Long idCursor;
    private String nameCursor;
    private Long memberCursor;
    private Long popularOrActivitiesCursor;
    private LocalDateTime createdAtCursor;

    public static CursorHolder fromId(Long cursor) {
        if (cursor == null) throw new NullPointerException("cursorId is null");
        CursorHolder holder = new CursorHolder();
        holder.idCursor = cursor;
        return holder;
    }

    public CursorHolder withName(String cursor) {
        this.nameCursor = cursor;
        return this;
    }

    public CursorHolder withMember(Long cursor) {
        this.memberCursor = cursor;
        return this;
    }

    public CursorHolder withCreatedAt(LocalDateTime cursor) {
        this.createdAtCursor = cursor;
        return this;
    }
    public CursorHolder withPopularOrActivitiesCursor(Long cursor) {
        this.popularOrActivitiesCursor = cursor;
        return this;
    }
}