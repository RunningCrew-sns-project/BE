package com.github.accountmanagementproject.web.dto.crew_join_post.location_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MetaDto {

    @JsonProperty("total_count")  // kakao 응답값(json) 형태와 현재 자바 형태를 매핑해줌, total_count 라는 형태로 맞춰줌
    private Integer totalCount;  // 검색된 문서 수
}
