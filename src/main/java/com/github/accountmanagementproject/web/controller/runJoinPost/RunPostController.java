package com.github.accountmanagementproject.web.controller.runJoinPost;

import com.github.accountmanagementproject.alarm.sse.SseEmitters;
import com.github.accountmanagementproject.repository.account.socialid.SocialId;
import com.github.accountmanagementproject.repository.account.user.myenum.OAuthProvider;
import com.github.accountmanagementproject.service.runJoinPost.RunPostService;
import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/test")
    public CustomSuccessResponse test(@RequestParam Long id, @RequestParam String message){
        sseEmitters.sendBySihuTest(id,new SocialId("메롱",OAuthProvider.GOOGLE));

        return new CustomSuccessResponse.SuccessDetail()
                .message("테스트 성공")
                .build();
    }


}
