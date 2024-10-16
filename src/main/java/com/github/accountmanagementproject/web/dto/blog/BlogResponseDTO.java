package com.github.accountmanagementproject.web.dto.blog;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogResponseDTO {
    private Integer id;
    private String title;
    private String content;
    private String record;
    private String imageUrl;
}
