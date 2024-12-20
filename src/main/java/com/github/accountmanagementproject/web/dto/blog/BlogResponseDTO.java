package com.github.accountmanagementproject.web.dto.blog;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogResponseDTO {
    private Integer blogId;

    private Long userId;
    private String userName;
    private String userImg;

    private String title;
    private String content;
    private String record;
    private String distance;
    private List<String> imageUrl;
    private Integer likeCount;
    private boolean liked;
    private LocalDateTime createdAt;
}
