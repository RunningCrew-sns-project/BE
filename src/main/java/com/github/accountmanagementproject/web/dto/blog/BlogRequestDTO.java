package com.github.accountmanagementproject.web.dto.blog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlogRequestDTO {
    private String title;
    private String content;
    private String record;
    private String distance;
}
