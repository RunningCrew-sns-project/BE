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
    private int size = 20;  // 기본값을 20으로 설정 (요청 없을 시)
    private String location = "전체";  // 필터 필드 (예: 전체, 역삼, 삼성 등)
    private LocalDate date = null;  // 기본 날짜: 오늘
    private String sortType = "newest"; // 정렬 방식: newest (최신순), oldest (오래된 순)

    public PageRequestDto() {
        this.size = 20;
        this.sortType = "newest";
    }
}
