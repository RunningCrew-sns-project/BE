package com.github.accountmanagementproject.web.controller.account;

import com.github.accountmanagementproject.service.crew.MyPageCrewAndPostService;
import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class MyPageCrewAndPostController implements MyPageCrewAndPostControllerDocs{
    private final MyPageCrewAndPostService myPageCrewAndPostService;

    @GetMapping("/my-crew")
    public CustomSuccessResponse getMyCrew(@AuthenticationPrincipal String email, @RequestParam(required = false) Boolean all){
        return new CustomSuccessResponse.SuccessDetail()
                .message("내 크루 조회 성공")
                .responseData(myPageCrewAndPostService.getMyCrew(email, all))
                .build();
    }
}
