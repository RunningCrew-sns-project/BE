package com.github.accountmanagementproject.web.dto.blog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlogRequestDTO {
    @Schema(description = "블로그 제목", example = "블로그 제목 입력")
    private String title;

    @Schema(description = "블로그 내용", example = "블로그 내용 입력")
    private String content;

    @Schema(description = "러닝 기록", example = "HH:mm:ss")
    private String record;

    @Schema(description = "달린 거리", example = "0.0km")
    private String distance;

    @Schema(description = "s3 이미지 url 리스트", example = "[\"이미지url1\", \"이미지url2\"]")
    private List<String> imageUrl;
}
