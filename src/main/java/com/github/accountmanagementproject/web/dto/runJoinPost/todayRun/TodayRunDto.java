package com.github.accountmanagementproject.web.dto.runJoinPost.todayRun;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodayRunDto {
    private Long id;
    private String title;
    private LocalDateTime startDate;
    private Boolean isCrew;
}
