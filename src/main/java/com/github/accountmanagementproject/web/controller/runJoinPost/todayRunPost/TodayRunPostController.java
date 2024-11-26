package com.github.accountmanagementproject.web.controller.runJoinPost.todayRunPost;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.service.runJoinPost.TodayRunService;
import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/today")
public class TodayRunPostController implements TodayRunPostControllerDocs{
    private final TodayRunService todayRunService;
    private final AccountConfig accountConfig;

    @Override
    @GetMapping
    public CustomSuccessResponse getMyTodayRunPost(@AuthenticationPrincipal String email) {
        MyUser user = accountConfig.findMyUser(email);
        return new CustomSuccessResponse.SuccessDetail()
                .message(todayRunService.getMyTodayRunPost(user).isEmpty() ? "참여중인 달리기가 없습니다." : "데이터를 조회했습니다.")
                .responseData(todayRunService.getMyTodayRunPost(user))
                .build();
    }

//    @Override
//    @PutMapping("/completeTodayGeneralRun/{runPostId}")
//    public CustomSuccessResponse completeTodayGeneralRun(@AuthenticationPrincipal String email,
//                                                         @PathVariable Long runPostId){
//        MyUser user = accountConfig.findMyUser(email);
//        return new CustomSuccessResponse.SuccessDetail()
//                .message("처리가 완료되었습니다.")
//                .responseData(todayRunService.completeTodayGeneralRun(user, runPostId))
//                .build();
//    }
//
//    @Override
//    @PutMapping("/completeTodayCrewRun/{runPostId}")
//    public CustomSuccessResponse completeTodayCrewRun(@AuthenticationPrincipal String email,
//                                                      @PathVariable Long runPostId){
//        MyUser user = accountConfig.findMyUser(email);
//        return new CustomSuccessResponse.SuccessDetail()
//                .message("처리가 완료되었습니다.")
//                .responseData(todayRunService.completeTodayCrewRun(user, runPostId))
//                .build();
//    }



}
