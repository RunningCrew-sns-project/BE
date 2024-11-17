package com.github.accountmanagementproject.service.account.auth.userdetailservice;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.exception.AccountLockedException;
import com.github.accountmanagementproject.exception.CustomAccessDenied;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.web.dto.account.auth.AuthFailureMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;


@Service
@Primary
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final AccountConfig accountConfig;
    private final HttpServletRequest request;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String emailOrPhoneNumber) {
        MyUser myUser = accountConfig.findMyUserFetchJoin(emailOrPhoneNumber);
        checkLockedOrDisable(myUser);

        HttpSession session = request.getSession();
        session.setAttribute("userId", myUser.getUserId());


        return User.builder()
                .username(myUser.getEmail())
                .password(myUser.getPassword())
                .authorities(myUser.getRoles().stream()
                        .map(roles->new SimpleGrantedAuthority(roles.getName().name()))
                        .collect(Collectors.toSet())
                )
//                .accountLocked(myUser.isLocked() && !myUser.isUnlockTime())
//                .disabled(myUser.isDisabled())
                .build();
    }

    private void checkLockedOrDisable(MyUser myUser) {
        if (myUser.isLocked() && !myUser.isUnlockTime()) {
            throw new AccountLockedException.ExceptionBuilder()
                    .customMessage("로그인 실패")
                    .request(new AuthFailureMessage(myUser))
                    .build();
        } else if (myUser.isDisabled()){
            throw new CustomAccessDenied.ExceptionBuilder()
                    .systemMessage("시스템")
                    .customMessage("로그인 실패")
                    .request(new AuthFailureMessage(myUser))
                    .build();
        }
    }
}
