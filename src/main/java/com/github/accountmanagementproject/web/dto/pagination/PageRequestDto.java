package com.github.accountmanagementproject.web.dto.pagination;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;


@Builder
@AllArgsConstructor
@Data
public class PageRequestDto {

    private int page = 0;  // 페이지 번호를 0으로 초기화 (무한 스크롤 첫 요청)
    private int size = 9;  // 한 번에 9개씩 불러오기
    private String location = "전체";  // 필터 필드 (예: 전체, 역삼, 삼성 등)
    private LocalDate date = null;  // 기본 날짜: 오늘

    public PageRequestDto() {
        this.page = 0;
        this.size = 9;
    }

    @JsonIgnore  // 스웨거에 노출되지 않도록 설정
    public Pageable getPageable() {
        return PageRequest.of(page, size, Sort.by("createdAt").descending());
    }

    // 다음 페이지 요청을 위한 page 증가 메서드
    public void nextPage() {
        this.page += 1;
    }
}
