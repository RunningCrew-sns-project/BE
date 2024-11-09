package com.github.accountmanagementproject.web.dto.pagination;

import lombok.Data;
import org.springframework.data.domain.Slice;

import java.util.List;


@Data
public class PageResponseDto<T> {

    private List<T> content;        // 현재 스크롤에 로드된 데이터
    private int countPerScroll;     // 스크롤 당 데이터 개수
    private boolean lastScroll;     // 다음 데이터 존재 여부
    private Integer nextCursor;     // 다음 커서 값 (다음 페이지 요청을 위한 마지막 항목의 ID)

    // 생성자
    public PageResponseDto(List<T> content, int countPerScroll, boolean lastScroll, Integer nextCursor) {
        this.content = content;
        this.countPerScroll = countPerScroll;
        this.lastScroll = lastScroll;
        this.nextCursor = nextCursor;
    }
}
