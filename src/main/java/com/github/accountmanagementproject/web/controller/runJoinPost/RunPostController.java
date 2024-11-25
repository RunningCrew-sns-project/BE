package com.github.accountmanagementproject.web.controller.runJoinPost;

import com.github.accountmanagementproject.alarm.sse.SseEmitters;
import com.github.accountmanagementproject.service.runJoinPost.RunPostService;
import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/run-post")
@Tag(name = "RunPost", description = "러닝 포스트 참여자 조회")
public class RunPostController {
    private final RunPostService runPostService;
    private final SseEmitters sseEmitters;


    @GetMapping("/users")
    @Operation(summary = "나의 런닝 포스트 참여자 조회", description = "isCrew는 크루달리기여부, isAll 은 true면 전체 , false면 요청상태만, null이면 가입완료상태만")
    public CustomSuccessResponse getMyRunPostUsers(@RequestParam(required = false) boolean isCrew, @RequestParam(required = false) Boolean isAll, @AuthenticationPrincipal String email){

        return new CustomSuccessResponse.SuccessDetail()
                .message("조회 성공")
                .responseData(runPostService.getRunPostUsers(isCrew, email, isAll))
                .build();
    }

    @DeleteMapping("/crew/{postId}/users/{badUserId}")
    @Operation(summary = "크루 달리기 멤버에서 크루원 강퇴", description = "크루원을 강퇴합니다.")
    public CustomSuccessResponse crewRunFromMemberDrop(@PathVariable @Parameter(description = "게시물 고유 번호") Long postId, @PathVariable @Parameter(description = "강퇴시킬 userId") Long badUserId, @AuthenticationPrincipal String authorEmail){
        runPostService.runFromMemberDrop(postId, badUserId, authorEmail, true);
        return new CustomSuccessResponse.SuccessDetail()
                .message("강퇴 성공")
                .build();
    }

    @DeleteMapping("/general/{postId}/users/{badUserId}")
    @Operation(summary = "일반 달리기 멤버에서 회원 강퇴", description = "달리기 회원을 강퇴합니다.")
    public CustomSuccessResponse generalRunFromMemberDrop(@PathVariable @Parameter(description = "게시물 고유 번호") Long postId, @PathVariable @Parameter(description = "강퇴시킬 userId") Long badUserId, @AuthenticationPrincipal String authorEmail){
        runPostService.runFromMemberDrop(postId, badUserId, authorEmail, false);
        return new CustomSuccessResponse.SuccessDetail()
                .message("강퇴 성공")
                .build();
    }



    @GetMapping("/test")
    public CustomSuccessResponse test(@RequestParam Long id, @RequestParam String message){
        sseEmitters.sendBySihuTest(id,message);

        return new CustomSuccessResponse.SuccessDetail()
                .message("테스트 성공")
                .build();
    }


}
