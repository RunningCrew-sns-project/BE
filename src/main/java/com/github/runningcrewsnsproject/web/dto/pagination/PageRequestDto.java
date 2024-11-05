package com.github.runningcrewsnsproject.web.dto.pagination;

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

//    private int page;
    private int page = 0;  // 페이지 번호를 0으로 초기화
    private final int size = 9;  // 한 번에 9개씩 불러오기
    private String location = "전체";  // 필터 필드 (예: 전체, 역삼, 삼성 등)
    private Long crewId;  // // 크루 아이디 필터 (null 가능)
    private LocalDate date = LocalDate.now();  // 기본 날짜: 오늘
    private boolean latestOrder = true;  // 최신순 정렬 여부

    public PageRequestDto() {
        this.page = 1;  // 첫 페이지를 1로 설정
    }

    public Pageable getPageable() {
        return PageRequest.of(page, size, Sort.by("createdAt").descending());
    }

    // 다음 페이지 요청을 위한 page 증가 메서드
    public void nextPage() {
        this.page += 1;
    }
}
