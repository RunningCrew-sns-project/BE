package com.github.runningcrewsnsproject.web.dto.chat;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Integer userId;
    private String userName;
}
