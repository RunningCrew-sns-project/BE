package com.github.accountmanagementproject.web.controller.account;

import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "마이페이지의 크루와 게시물", description = "마이페이지 크루 및 게시글 관련 API")
public interface MyPageCrewAndPostControllerDocs {

    @Operation(summary = "내 크루 조회", description = "내가 만든 크루와 가입한 크루를 조회합니다.")
    CustomSuccessResponse getMyCrew(String email, @Parameter(name = "조회 조건", description = "null = 내가 만든 크루와 가입완료 크루, true = 전체, false = 요청중인 크루") Boolean isAll);
}