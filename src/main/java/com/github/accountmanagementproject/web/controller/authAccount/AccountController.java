package com.github.accountmanagementproject.web.controller.authAccount;

import com.github.accountmanagementproject.service.authAccount.AccountService;
import com.github.accountmanagementproject.web.dto.accountAuth.myPage.AccountInfoDto;
import com.github.accountmanagementproject.web.dto.responseSystem.CustomSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Validated
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController implements AccountControllerDocs{
    private final AccountService accountService;
    @GetMapping("/my-info")
    public CustomSuccessResponse getMyInfo(@AuthenticationPrincipal String principal){

        return new CustomSuccessResponse.SuccessDetail()
                .message("유저 정보 조회 성공")
                .responseData(accountService.myInfoByEmail(principal))
                .build();
    }
    @GetMapping
    public CustomSuccessResponse myPageMain(@AuthenticationPrincipal String principal){
        return new CustomSuccessResponse.SuccessDetail()
                .message("마이페이지 메인 조회 성공")
                .responseData(accountService.getMyPageResponse(principal))
                .build();
    }
    @PutMapping("/my-info")
    public CustomSuccessResponse updateMyInfo(@AuthenticationPrincipal String principal, AccountInfoDto accountInfoDto){
        return new CustomSuccessResponse.SuccessDetail()
                .message("유저 정보 수정 성공")
                .responseData(accountService.myInfoEdit(principal))
                .build();
    }

}
