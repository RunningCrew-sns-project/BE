package com.github.accountmanagementproject.web.dto.runRecords;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RunRecordsRequestDto {
    @Schema(description = "기록", example = "HH:mm:ss")
    private String record;

    @Schema(description = "거리", example = "0.0km")
    private String distance;

    @Schema(description = "완료율", example = "100")
    private Integer progress;
}
