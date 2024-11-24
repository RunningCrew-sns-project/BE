package com.github.accountmanagementproject.web.controller.runJoinPost.todayRunPost;

import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Today Run Post",description = "오늘의 달리기 관련 API")
public interface TodayRunPostControllerDocs {
    @Operation(summary = "오늘의 달리기 조회")
    @GetMapping
    CustomSuccessResponse getMyTodayRunPost(@AuthenticationPrincipal String email);

}
