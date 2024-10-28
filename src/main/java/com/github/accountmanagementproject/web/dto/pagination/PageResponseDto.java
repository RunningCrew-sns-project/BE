package com.github.accountmanagementproject.web.dto.pagination;

import lombok.Data;
import org.springframework.data.domain.Slice;

import java.util.List;


@Data
public class PageResponseDto<T> {

    private List<T> content;
    private int currentPage;
    private int size;
    private boolean first;
    private boolean last;

    public PageResponseDto(Slice<T> sliceContent) {
        this.content = sliceContent.getContent();
        this.currentPage = sliceContent.getNumber();
        this.size = sliceContent.getSize();
        this.first = sliceContent.isFirst();
        this.last = sliceContent.isLast();
    }
}
