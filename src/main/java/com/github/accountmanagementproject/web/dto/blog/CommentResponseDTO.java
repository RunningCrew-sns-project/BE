package com.github.accountmanagementproject.web.dto.blog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private Integer commentId;

    private Integer blogId;

    private Long userId;
    private String userName;
    private String userImg;

    private String content;

    private LocalDateTime createdAt;
}
