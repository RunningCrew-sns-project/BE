package com.github.runningcrewsnsproject.web.dto.blog;

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
    private Integer blogId;
    private Integer commentId;
    private String content;
    private String userName;
    private String userImg;
    private LocalDateTime createdAt;
}
