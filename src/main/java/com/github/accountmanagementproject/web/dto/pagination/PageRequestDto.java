package com.github.accountmanagementproject.web.dto.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@Builder
@AllArgsConstructor
@Data
public class PageRequestDto {

    private int page;
    private final int size = 9;  // 한 번에 9개씩 불러오기
    private String location;  // 필터 필드 (예: 전체, 역삼, 삼성 등)

    public PageRequestDto() {
        this.page = 1;  // 첫 페이지를 1로 설정
    }

    public Pageable getPageable(Sort sort) {
        return PageRequest.of(page - 1, size, sort);
    }

    // 다음 페이지 요청을 위한 page 증가 메서드
    public void nextPage() {
        this.page += 1;
    }
}
