package com.github.accountmanagementproject.service.account;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.service.mapper.user.UserMapper;
import com.github.accountmanagementproject.web.dto.account.mypage.AccountInfoResponse;
import com.github.accountmanagementproject.web.dto.account.mypage.AccountModifyRequest;
import com.github.accountmanagementproject.web.dto.account.mypage.AccountSummary;
import com.github.accountmanagementproject.web.dto.account.mypage.TempInfoModifyForFigma;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountConfig accountConfig;

    //로그인 한 유저 정보 조회
    public AccountInfoResponse myInfoByEmail(String principal) {
        MyUser myUser = accountConfig.findMyUserFetchJoin(principal);
        return UserMapper.INSTANCE.myUserToAccountInfoResponse(myUser);
    }
    //마이페이지 상단에 표시할 간단한 개인정보 조회
    public AccountSummary getMyPageTopInfoResponse(String principal) {
        MyUser myUser = accountConfig.findMyUser(principal);
        return UserMapper.INSTANCE.myUserToAccountSummary(myUser);
    }
    //개인정보 수정
    @Transactional
    public AccountInfoResponse myInfoEdit(String principal, AccountModifyRequest modifyRequest) {
        modifyRequest.defaultProfileUrlSetting(modifyRequest.getGender());
        MyUser updatedUser = accountConfig.findMyUser(principal).updateUserInfo(modifyRequest);
        return UserMapper.INSTANCE.myUserToAccountInfoResponse(updatedUser);
    }
    //회원탈퇴
    @Transactional
    public void userWithdrawalProcessing(String principal) {
        accountConfig.findMyUser(principal).withdrawalProcessing();
    }

    //마이페이지 상단에 표시할 간단한 개인정보 수정
    @Transactional
    public AccountSummary mySummaryInfoEdit(String principal, AccountSummary accountSummary) {
        MyUser user = accountConfig.findMyUser(principal);
        accountSummary.defaultProfileUrlSetting(user.getGender());
        user.updateUserSummaryInfo(accountSummary);
        return UserMapper.INSTANCE.myUserToAccountSummary(user);
    }
    @Transactional
    public TempInfoModifyForFigma myInfoEditForFigma(String principal, TempInfoModifyForFigma modifyRequest) {
        MyUser user = accountConfig.findMyUser(principal);
        modifyRequest.defaultProfileUrlSetting(user.getGender());
        user.updateUserInfoForFigma(modifyRequest);
        return UserMapper.INSTANCE.myUserToTempInfoModifyForFigma(user);
    }
}
