package com.github.accountmanagementproject.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    UNAUTHORIZED_POST_VIEW(HttpStatus.FORBIDDEN, "해당 게시물을 조회할 권한이 없습니다."),
    UNAUTHORIZED_POST_EDIT(HttpStatus.FORBIDDEN, "게시글을 수정할 권한이 없습니다."),
    UNAUTHORIZED_POST_EDIT_AUTHOR_ONLY(HttpStatus.FORBIDDEN, "게시글 작성자만 수정할 수 있습니다."),
    UNAUTHORIZED_POST_DELETE(HttpStatus.FORBIDDEN, "게시글을 삭제할 권한이 없습니다."),
    UNAUTHORIZED_POST_DELETE_AUTHOR_ONLY(HttpStatus.FORBIDDEN, "게시글 작성자만 삭제할 수 있습니다."),
    STORAGE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 수정 중 오류가 발생했습니다."), // 이미지 수정 관련
    STORAGE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 삭제 중 오류가 발생했습니다."), // 이미지 삭제 관련
    UNAUTHORIZED_CREW_VIEW(HttpStatus.FORBIDDEN, "크루 목록을 조회할 권한이 없습니다."),
    UNAUTHORIZED_POST_CREATION(HttpStatus.FORBIDDEN, "게시글을 작성할 권한이 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not founded"),
    CREW_NOT_FOUND(HttpStatus.NOT_FOUND, "크루를 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),

    ;

    private final HttpStatus status;
    private final String message;

}
