package com.github.accountmanagementproject.service.authAccount;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.service.mappers.UserMapper;
import com.github.accountmanagementproject.web.dto.accountAuth.myPage.AccountInfoDto;
import com.github.accountmanagementproject.web.dto.accountAuth.myPage.AccountMain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountConfig accountConfig;

    public AccountInfoDto myInfoByEmail(String principal) {
        MyUser myUser = accountConfig.findMyUserFetchJoin(principal);
        return UserMapper.INSTANCE.myUserToAccountDto(myUser);
    }

    public AccountMain getMyPageResponse(String principal) {
        MyUser myUser = accountConfig.findMyUser(principal);
        return UserMapper.INSTANCE.myUserToAccountMain(myUser);
    }

    public AccountInfoDto myInfoEdit(String principal) {
        MyUser myUser = accountConfig.findMyUserFetchJoin(principal);


    }
}
