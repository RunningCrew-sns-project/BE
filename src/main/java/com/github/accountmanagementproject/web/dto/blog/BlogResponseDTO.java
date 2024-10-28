package com.github.accountmanagementproject.web.dto.blog;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogResponseDTO {
    private String userName;
    private String userImg;

    private Integer blogId;
    private String title;
    private String content;
    private String record;
    private String distance;
    private String imageUrl;
    private Integer likeCount;
    private boolean liked;
    private LocalDateTime createdAt;
}
