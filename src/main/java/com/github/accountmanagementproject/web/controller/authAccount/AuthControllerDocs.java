package com.github.accountmanagementproject.web.controller.authAccount;

import com.github.accountmanagementproject.web.dto.accountAuth.AccountDto;
import com.github.accountmanagementproject.web.dto.accountAuth.LoginRequest;
import com.github.accountmanagementproject.web.dto.accountAuth.TokenDto;
import com.github.accountmanagementproject.web.dto.response.CustomSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "회원 인증 관련 API")
public interface AuthControllerDocs {
    @Operation(summary = "회원 가입", description = "회원 가입에 필요한 정보들을 입력 받아 가입 진행")
    @ApiResponse(responseCode = "201",description = "회원 가입 성공",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "페이지 조회 성공 예",
                                    description = "⬆️⬆️ data 안에 posts 배열로 목록들을 반환해주고<br> totalPosts는 총 게시물, totalPages는 총 페이지수 입니다. ",
                                    value = "{\n" +
                                            "  \"code\": 200,\n" +
                                            "  \"message\": \"OK\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"posts\": [\n" +
                                            "      \"~~게시물 목록 배열들~~ 생략\"\n" +
                                            "    ],\n" +
                                            "    \"totalPosts\": 10,\n" +
                                            "    \"totalPages\": 1\n" +
                                            "  }\n" +
                                            "}")
                    })
    )
    @ApiResponse(responseCode = "400", description = "검색어 최소 글자수 미 충족",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "최소 2글자 이상이어야 합니다.",
                                    description = "⬆️⬆️ 검색어는 최소 2글자 이상이어야함.",
                                    value = """
                                             {
                                              "code": 400,
                                              "message": "BAD_REQUEST",
                                              "detailMessage": "검색어는 2글자 이상이어야 합니다.",
                                              "request": "이"
                                            }""")
                    })
    )
    @ApiResponse(responseCode = "404", description = "존재하지 않는 페이지 (totalPages를 넘어가는 page로 요청한 경우)",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "페이지 조회 실패 예제",
                                    description = "⬆️⬆️ 총 페이지 수 보다 더 큰 숫자의 페이지를 요청한 경우 발생되는 익셉션 입니다.<br>" +
                                            "request에는 요청들어온 숫자가 반환됩니다.<br>" +
                                            "예제에서는 99페이지를 요청했고 99페이지는 존재 하지않아 에러가 발생된 상황.",
                                    value = "{\n" +
                                            "  \"code\": 404,\n" +
                                            "  \"message\": \"NOT_FOUND\",\n" +
                                            "  \"detailMessage\": \"Page Not Found\",\n" +
                                            "  \"request\": 99\n" +
                                            "}")
                    })
    )     ResponseEntity<CustomSuccessResponse> signUp(@RequestBody AccountDto accountDto);
    @PostMapping("/login")
     CustomSuccessResponse login(@RequestBody LoginRequest loginRequest);
    @PostMapping("/refresh")
    CustomSuccessResponse regenerateToken(@RequestBody TokenDto tokenDto);
}
