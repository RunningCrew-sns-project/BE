package com.github.accountmanagementproject.web.dto.pagination;

import lombok.Data;
import org.springframework.data.domain.Slice;

import java.util.List;


@Data
public class PageResponseDto<T> {

    private List<T> content;
    private int currentPage;     // 현재 페이지 번호
    private int size;            // 페이지 당 데이터 개수
    private boolean hasMore;      // 다음 페이지 존재 여부
//    private boolean first;
//    private boolean last;

    public PageResponseDto(Slice<T> sliceContent) {
        this.content = sliceContent.getContent();
        this.currentPage = sliceContent.getNumber();
        this.size = sliceContent.getSize();
        this.hasMore = sliceContent.hasNext();  // 다음 페이지가 있는지 여부
//        this.first = sliceContent.isFirst();
//        this.last = sliceContent.isLast();
    }
}
