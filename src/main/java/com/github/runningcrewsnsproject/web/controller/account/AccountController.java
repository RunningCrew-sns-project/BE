package com.github.runningcrewsnsproject.web.controller.account;

import com.github.runningcrewsnsproject.service.account.AccountService;
import com.github.runningcrewsnsproject.web.dto.account.mypage.AccountModifyRequest;
import com.github.runningcrewsnsproject.web.dto.account.mypage.AccountSummary;
import com.github.runningcrewsnsproject.web.dto.account.mypage.TempInfoModifyForFigma;
import com.github.runningcrewsnsproject.web.dto.responsebuilder.CustomSuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/my-summary")
    public CustomSuccessResponse simpleInfoInquiry(@AuthenticationPrincipal String principal){
        return new CustomSuccessResponse.SuccessDetail()
                .message("마이페이지 상단 간단 정보 조회 성공")
                .responseData(accountService.getMyPageTopInfoResponse(principal))
                .build();
    }
    @PutMapping("/my-summary")
    public CustomSuccessResponse simpleInfoModify(@AuthenticationPrincipal String principal, @RequestBody @Valid AccountSummary accountSummary){
        return new CustomSuccessResponse.SuccessDetail()
                .message("마이페이지 상단 간단 정보 수정 성공")
                .responseData(accountService.mySummaryInfoEdit(principal,accountSummary))
                .build();
    }

    @PutMapping("/my-info")
    public CustomSuccessResponse updateMyInfo(@AuthenticationPrincipal String principal,@RequestBody @Valid AccountModifyRequest modifyRequest){
        return new CustomSuccessResponse.SuccessDetail()
                .message("유저 정보 수정 성공")
                .responseData(accountService.myInfoEdit(principal, modifyRequest))
                .build();
    }
    @DeleteMapping("/my-info")
    public CustomSuccessResponse withdrawalFunction(@AuthenticationPrincipal String principal){
        accountService.userWithdrawalProcessing(principal);
        return new CustomSuccessResponse.SuccessDetail()
                .message("유저 탈퇴 처리 성공")
                .build();
    }

    //피그마버전 개인정보 수정
    @PutMapping("/my-info/figma")
    public CustomSuccessResponse updateMyInfoFigma(@AuthenticationPrincipal String principal,@RequestBody @Valid TempInfoModifyForFigma modifyRequest){
        return new CustomSuccessResponse.SuccessDetail()
                .message("유저 정보 수정 성공")
                .responseData(accountService.myInfoEditForFigma(principal, modifyRequest))
                .build();
    }
}
