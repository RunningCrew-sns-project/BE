package com.github.accountmanagementproject.service.account.auth.oauth;

import com.github.accountmanagementproject.config.client.dto.userInfo.OAuthUserInfo;
import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.config.security.JwtProvider;
import com.github.accountmanagementproject.exception.CustomBadRequestException;
import com.github.accountmanagementproject.exception.CustomNotFoundException;
import com.github.accountmanagementproject.exception.CustomServerException;
import com.github.accountmanagementproject.repository.account.socialid.SocialId;
import com.github.accountmanagementproject.repository.account.socialid.SocialIdPk;
import com.github.accountmanagementproject.repository.account.socialid.SocialIdsRepository;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.service.mapper.user.UserMapper;
import com.github.accountmanagementproject.web.dto.account.auth.TokenDto;
import com.github.accountmanagementproject.web.dto.account.auth.oauth.request.OAuthLoginParams;
import com.github.accountmanagementproject.web.dto.account.auth.oauth.request.OAuthSignUpRequest;
import com.github.accountmanagementproject.web.dto.account.auth.oauth.response.AuthResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {
    private final MyUsersRepository myUsersRepository;
    private final SocialIdsRepository socialIdsRepository;
    private final AccountConfig accountConfig;
    private final com.github.accountmanagementproject.service.account.auth.oauth.OAuthClientManager oAuthClientManager;
    private final JwtProvider jwtProvider;

    @Transactional
    public AuthResult<?> loginOrCreateTempAccount(OAuthLoginParams params) {
        OAuthUserInfo oAuthUserInfo = oAuthClientManager.request(params);
        Optional<MyUser> myUserOptional = myUsersRepository.findBySocialIdPk(new SocialIdPk(oAuthUserInfo.getSocialId(),oAuthUserInfo.getOAuthProvider()));
        MyUser myUser = myUserOptional.orElseGet(() -> processSignUp(oAuthUserInfo));
        return myUserOptional.isPresent() & !myUser.isDisabled() ? createOAuthLoginResponse(myUser) : createOAuthSignUpResponse(oAuthUserInfo);

    }

    private AuthResult<OAuthSignUpRequest> createOAuthSignUpResponse(OAuthUserInfo oAuthUserInfo) {
        return AuthResult.<OAuthSignUpRequest>builder()
                .response( UserMapper.INSTANCE.oAuthUserInfoToOAuthSignUpDto(oAuthUserInfo) )
                .message("임시 계정 생성")
                .httpStatus(HttpStatus.CREATED)
                .build();
    }
    private AuthResult<TokenDto> createOAuthLoginResponse(MyUser myUser) {
        return AuthResult.<TokenDto>builder()
                .response(createTokenAndSave(myUser))
                .message("로그인 성공")
                .httpStatus(HttpStatus.OK)
                .build();
    }


    private TokenDto createTokenAndSave(MyUser myUser) {
        String roles = myUser.getRoles().stream().map(role -> role.getName().name())
                .collect(Collectors.joining(","));
        //토큰 생성
        String accessToken = jwtProvider.createNewAccessToken(myUser.getEmail(), roles);
        String refreshToken = jwtProvider.createNewRefreshToken();
        try {
            myUser.loginValueSetting(false);
            return jwtProvider.saveRefreshTokenAndCreateTokenDto(myUser.getUserId(),accessToken, refreshToken, Duration.ofMinutes(3));
        } catch (RedisConnectionFailureException e) {
            throw new CustomServerException.ExceptionBuilder()
                    .systemMessage(e.getMessage())
                    .customMessage("Redis 서버 연결 실패")
                    .build();
        }
    }

    private MyUser processSignUp(OAuthUserInfo oAuthUserInfo) {
        MyUser newUser = UserMapper.INSTANCE.oAuthInfoResponseToMyUser(oAuthUserInfo);
        newUser.setRoles(Set.of(accountConfig.getNormalUserRole()));
        newUser.getSocialIds().iterator().next().setMyUser(newUser);

        return myUsersRepository.save(newUser);
    }

    @Transactional
    public void signUp(OAuthSignUpRequest oAuthSignUpRequest) {

        SocialId socialId = socialSignUpCheckAndFindSocialId(oAuthSignUpRequest);
        oAuthSignUpRequest.defaultProfileUrlSetting(oAuthSignUpRequest.getGender());
        try {
            socialId.socialConnectSetting();
            socialId.getMyUser().oAuthSignUpSetting(oAuthSignUpRequest);
        } catch (DateTimeException e) {
            throw new CustomBadRequestException.ExceptionBuilder()
                    .systemMessage(e.getMessage())
                    .customMessage("호환되지 않는 날짜 형식 (exists. yyyy-M-d)")
                    .request(oAuthSignUpRequest.getDateOfBirth())
                    .build();
        }
    }
    private SocialId socialSignUpCheckAndFindSocialId(OAuthSignUpRequest oAuthSignUpRequest){
        SocialId socialId = socialIdsRepository.findBySocialIdPkJoinMyUser(new SocialIdPk(oAuthSignUpRequest.getSocialId(), oAuthSignUpRequest.getProvider()))
                .orElseThrow(() -> new CustomNotFoundException.ExceptionBuilder()
                        .customMessage("임시 계정이 존재하지 않습니다.")
                        .request(oAuthSignUpRequest)
                        .systemMessage("NotFoundException")
                        .build());
        if( socialId.getMyUser().isEnabled() )
            throw new CustomBadRequestException.ExceptionBuilder()
                    .customMessage("이미 가입된 계정입니다.")
                    .request(oAuthSignUpRequest)
                    .build();
        return socialId;
    }
}
