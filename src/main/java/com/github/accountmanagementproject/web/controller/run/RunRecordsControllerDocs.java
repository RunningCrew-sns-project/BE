package com.github.accountmanagementproject.web.controller.run;

import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import com.github.accountmanagementproject.web.dto.runRecords.RunRecordsRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "RunRecordsController", description = "달리기 기록 CRUD API")
public interface RunRecordsControllerDocs {
    @Operation(summary = "달리기 기록 조회", description = "달리기 기록 조회, 무한스크롤링 페이지네이션")
    @Parameters({
            @Parameter(name = "size", description = "한번에 보여질 갯수"),
            @Parameter(name = "cursor", description = "시작할 기록 번호"),
    })
    @GetMapping
    CustomSuccessResponse getRunRecords(
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer cursor,
            @AuthenticationPrincipal String email);

    @Operation(summary = "달리기 기록 작성", description = "달리기 기록 작성")
    @PostMapping("/writeRunRecords")
    CustomSuccessResponse postRunRecords(
            @RequestBody RunRecordsRequestDto runRecordsRequestDto,
            @AuthenticationPrincipal String email
    );

    @Operation(summary = "달리기 기록 수정", description = "달리기 기록 수정")
    @Parameter(name = "id", description = "수정할 기록 아이디")
    @PutMapping("/modifyRunRecords")
    CustomSuccessResponse putRunRecords(
            @RequestBody RunRecordsRequestDto runRecordsRequestDto,
            @RequestParam Integer id,
            @AuthenticationPrincipal String email
    );

    @Operation(summary = "달리기 기록 삭제", description = "달리기 기록 삭제")
    @Parameter(name = "id", description = "삭제할 기록 아이디")
    @DeleteMapping("/deleteRunRecords")
    CustomSuccessResponse deleteRunRecords(
            @RequestParam Integer id,
            @AuthenticationPrincipal String email
    );
}
