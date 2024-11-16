package com.github.accountmanagementproject.web.dto.runRecords;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RunRecordsDto {
    private Integer id;
    private Long userId;
    private String record;
    private String distance;
    private Integer progress;
    private LocalDateTime createdAt;
}
