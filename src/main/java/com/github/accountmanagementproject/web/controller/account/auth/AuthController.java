package com.github.accountmanagementproject.web.controller.account.auth;


import com.github.accountmanagementproject.service.account.auth.SignUpLoginService;
import com.github.accountmanagementproject.service.account.auth.oauth.OAuthLoginService;
import com.github.accountmanagementproject.web.dto.account.auth.LoginRequest;
import com.github.accountmanagementproject.web.dto.account.auth.SignUpRequest;
import com.github.accountmanagementproject.web.dto.account.auth.TokenDto;
import com.github.accountmanagementproject.web.dto.account.auth.oauth.request.KakaoLoginParams;
import com.github.accountmanagementproject.web.dto.account.auth.oauth.request.NaverLoginParams;
import com.github.accountmanagementproject.web.dto.account.auth.oauth.request.OAuthLoginParams;
import com.github.accountmanagementproject.web.dto.account.auth.oauth.request.OAuthSignUpRequest;
import com.github.accountmanagementproject.web.dto.account.auth.oauth.response.AuthResult;
import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {
    private final SignUpLoginService signUpLoginService;
    private final OAuthLoginService oAuthLoginService;



    @Override
    @PostMapping("/sign-up")
    public ResponseEntity<CustomSuccessResponse> signUp(@RequestBody @Valid SignUpRequest signUpRequest){
        signUpLoginService.signUp(signUpRequest);
        CustomSuccessResponse signUpResponse = createSignUpResponse();
        return new ResponseEntity<>(signUpResponse, signUpResponse.getSuccess().getHttpStatus());
    }

    @Override
    @PostMapping("/login")
    public CustomSuccessResponse login(@RequestBody @Valid LoginRequest loginRequest){
        return new CustomSuccessResponse.SuccessDetail()
                .message("로그인 성공")
                .httpStatus(HttpStatus.OK)
                .responseData(signUpLoginService.loginResponseToken(loginRequest))
                .build();
    }


    @Override
    @PostMapping("/refresh")
    public CustomSuccessResponse regenerateToken(@RequestBody TokenDto tokenDto){
        return new CustomSuccessResponse.SuccessDetail()
                .message("토큰 재발급")
                .httpStatus(HttpStatus.OK)
                .responseData(signUpLoginService.refreshTokenByTokenDto(tokenDto))
                .build();
    }

    @PostMapping("/kakao")
    public ResponseEntity<CustomSuccessResponse> loginKakao(@RequestBody KakaoLoginParams params) {
        CustomSuccessResponse result = loginOAuth(params);
        return new ResponseEntity<>(result, result.getSuccess().getHttpStatus());
    }

    @PostMapping("/naver")
    public ResponseEntity<CustomSuccessResponse> loginNaver(@RequestBody NaverLoginParams params) {
        CustomSuccessResponse result = loginOAuth(params);
        return new ResponseEntity<>(result, result.getSuccess().getHttpStatus());
    }

    private CustomSuccessResponse loginOAuth(OAuthLoginParams params) {
        AuthResult<?> result = oAuthLoginService.loginOrCreateTempAccount(params);
        return new CustomSuccessResponse.SuccessDetail()
                .message(result.getMessage())
                .httpStatus(result.getHttpStatus())
                .responseData(result.getResponse())
                .build();
    }

    @PostMapping("/oauth")
    public ResponseEntity<CustomSuccessResponse> oAuthSignUp(@RequestBody @Valid OAuthSignUpRequest oAuthSignUpRequest) {
        oAuthLoginService.signUp(oAuthSignUpRequest);
        CustomSuccessResponse signUpResponse = createSignUpResponse();
        return new ResponseEntity<>(signUpResponse, signUpResponse.getSuccess().getHttpStatus());
    }

    private CustomSuccessResponse createSignUpResponse(){
        return new CustomSuccessResponse.SuccessDetail()
                .message("회원가입 완료")
                .httpStatus(HttpStatus.CREATED)
                .build();
    }



}
