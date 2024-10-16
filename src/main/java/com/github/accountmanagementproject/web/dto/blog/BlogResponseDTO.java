package com.github.accountmanagementproject.web.dto.blog;

import lombok.*;

import java.util.List;

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
    private String distance;
    private String imageUrl;
    private Integer likeCount;
    private String nickname;
    private List<BlogCommentResponseDTO> comments;
}
