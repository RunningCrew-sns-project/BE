package com.github.accountmanagementproject.web.dto.blog;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogDetails {
    private String userImg;
    private String userName;

    private Integer blogId;
    private String title;
    private String content;
    private String record;
    private String distance;
    private List<String> imageUrl;
    private Integer likeCount;
    private boolean liked;

    private LocalDateTime createdAt;
}
