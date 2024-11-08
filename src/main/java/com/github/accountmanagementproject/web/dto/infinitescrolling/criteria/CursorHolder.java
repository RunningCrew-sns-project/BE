package com.github.accountmanagementproject.web.dto.infinitescrolling.criteria;

import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class CursorHolder {
    private Long idCursor;
    private String nameCursor;
    private Integer memberCursor;
    private LocalDateTime createdAtCursor;

    public static CursorHolder fromId(Long cursor) {
        CursorHolder holder = new CursorHolder();
        holder.idCursor = cursor;
        return holder;
    }

    public static CursorHolder fromName(String cursor) {
        CursorHolder holder = new CursorHolder();
        holder.nameCursor = cursor;
        return holder;
    }

    public static CursorHolder fromMember(Integer cursor) {
        CursorHolder holder = new CursorHolder();
        holder.memberCursor = cursor;
        return holder;
    }

    public static CursorHolder fromCreatedAt(LocalDateTime cursor) {
        CursorHolder holder = new CursorHolder();
        holder.createdAtCursor = cursor;
        return holder;
    }
}
