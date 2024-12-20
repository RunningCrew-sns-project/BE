package com.github.accountmanagementproject.service.account.auth;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.config.security.JwtProvider;
import com.github.accountmanagementproject.exception.CustomBadCredentialsException;
import com.github.accountmanagementproject.exception.CustomBadRequestException;
import com.github.accountmanagementproject.exception.CustomServerException;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.service.mapper.user.UserMapper;
import com.github.accountmanagementproject.web.dto.account.auth.LoginRequest;
import com.github.accountmanagementproject.web.dto.account.auth.SignUpRequest;
import com.github.accountmanagementproject.web.dto.account.auth.TokenDto;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SignUpLoginService {

    private final MyUsersRepository myUsersRepository;
    private final AccountConfig accountConfig;
    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final HttpServletRequest request;


    @Transactional
    public void signUp(SignUpRequest signUpRequest) {

        //비번 암호화
        signUpRequest.passwordEncryption(passwordEncoder.encode(signUpRequest.getPassword()));
        //기본프사설정
        signUpRequest.defaultProfileUrlSetting(signUpRequest.getGender());
        //세이브 실행하면서 중복값 발생시 발생되는 익셉션 예외처리
        try {
            MyUser signUpMyUser = UserMapper.INSTANCE.accountDtoToMyUser(signUpRequest);
            signUpMyUser.setRoles(Set.of(accountConfig.getNormalUserRole()));
            myUsersRepository.save(signUpMyUser);
        }catch (DateTimeException e){
            throw new CustomBadRequestException.ExceptionBuilder()
                    .systemMessage(e.getMessage())
                    .customMessage("호환되지 않는 날짜 형식 (exists. yyyy-M-d)")
                    .request(signUpRequest.getDateOfBirth())
                    .build();
        }
    }

    public TokenDto loginResponseToken(LoginRequest loginRequest) {
            Authentication authentication = authenticationManager.authenticate(loginRequest.toAuthentication());
            String roles = authentication.getAuthorities().stream()
                .map(authority->authority.getAuthority())
                .collect(Collectors.joining(","));
            String accessToken = jwtProvider.createNewAccessToken(authentication.getName(), roles);
            String refreshToken = jwtProvider.createNewRefreshToken();


            try {
                return jwtProvider.saveRefreshTokenAndCreateTokenDto(request.getSession().getAttribute("userId"), accessToken, refreshToken, Duration.ofMinutes(3));
            }catch (RedisConnectionFailureException e){
                e.printStackTrace();
                throw new CustomServerException.ExceptionBuilder()
                        .systemMessage(e.getMessage())
                        .customMessage("Redis 서버 연결 실패")
                        .build();
            }
    }

    public TokenDto refreshTokenByTokenDto(TokenDto tokenDto) {
        try{
            return jwtProvider.tokenRefresh(tokenDto);
        }catch (RedisConnectionFailureException e){
            throw new CustomServerException.ExceptionBuilder()
                    .systemMessage(e.getMessage())
                    .customMessage("Redis 서버 연결 실패")
                    .request(tokenDto)
                    .build();
        }catch (ExpiredJwtException | NoSuchElementException e){
            throw new CustomBadCredentialsException.ExceptionBuilder()
                    .systemMessage(e.getMessage())
                    .customMessage(e instanceof ExpiredJwtException ? "리프레시 토큰 만료" : "재발급 받을 수 없는 액세스 토큰")
                    .request(tokenDto)
                    .build();
        }

    }

    public void errorTest() {
        MyUser user = myUsersRepository.findByEmail("abc@abc.com").orElseThrow();
        myUsersRepository.delete(user);
    }
}
