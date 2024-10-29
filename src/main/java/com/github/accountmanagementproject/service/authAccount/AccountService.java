package com.github.accountmanagementproject.service.authAccount;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.service.mappers.user.UserMapper;
import com.github.accountmanagementproject.web.dto.accountAuth.myPage.account.AccountInfoResponse;
import com.github.accountmanagementproject.web.dto.accountAuth.myPage.account.AccountModifyRequest;
import com.github.accountmanagementproject.web.dto.accountAuth.myPage.account.AccountSummary;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        if(modifyRequest.getProfileImg() == null){
            String defaultProfileUrl = modifyRequest.defaultProfileUrl(modifyRequest.getGender());
            modifyRequest.setProfileImg(defaultProfileUrl);
        }
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
        if(accountSummary.getProfileImg() == null){
            String defaultProfileUrl = accountSummary.defaultProfileUrl(user.getGender());
            accountSummary.setProfileImg(defaultProfileUrl);
        }
        user.updateUserSummaryInfo(accountSummary);
        return UserMapper.INSTANCE.myUserToAccountSummary(user);
    }
}
