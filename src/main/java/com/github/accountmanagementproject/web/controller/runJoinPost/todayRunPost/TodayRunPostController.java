package com.github.accountmanagementproject.web.controller.runJoinPost.todayRunPost;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.service.runJoinPost.TodayRunService;
import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/today")
public class TodayRunPostController {
    private final TodayRunService todayRunService;
    private final AccountConfig accountConfig;

    public TodayRunPostController(TodayRunService todayRunService, AccountConfig accountConfig) {
        this.todayRunService = todayRunService;
        this.accountConfig = accountConfig;
    }

    @GetMapping
    public CustomSuccessResponse getMyTodayRunPost(@AuthenticationPrincipal String email) {
        MyUser user = accountConfig.findMyUser(email);
        return new CustomSuccessResponse.SuccessDetail()
                .responseData(todayRunService.getMyTodayRunPost(user))
                .build();
    }
}
