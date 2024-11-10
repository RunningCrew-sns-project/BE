package com.github.accountmanagementproject.web.dto.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;


@Builder
@AllArgsConstructor
@Data
public class PageRequestDto {

    private Integer cursor = null;  // 커서: 마지막 조회 항목의 ID, 첫 요청 시에는 null
    private int size = 10;  // 한 번에 9개씩 불러오기
    private String location = "전체";  // 필터 필드 (예: 전체, 역삼, 삼성 등)
    private LocalDate date = null;  // 기본 날짜: 오늘

    public PageRequestDto() {
        this.size = 10;
    }
}
