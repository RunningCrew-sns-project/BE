package com.github.accountmanagementproject.web.controller.blog;

import com.github.accountmanagementproject.web.dto.blog.BlogRequestDTO;
import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "Blog", description = "블로그 작성, 좋아요, 댓글 관련 API")
public interface BlogControllerDocs {


    //블로그 조회
    //TODO : 내가 쓴 글만 보여지게 할지? 필터링 구현 필요할듯
    @Operation(summary = "블로그 조회", description = "모든 블로그 조회, 무한스크롤링 페이지네이션")
    @ApiResponse(responseCode = "200",description = "블로그 조회 성공",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "블로그 조회 성공 예",
                                    description = "블로그 조회에 성공하여 DB에 있는 블로그의 내용이 응답값으로 반환됩니다.",
                                    value = "{\n" +
                                            "  \"code\": 200,\n" +
                                            "  \"httpStatus\": \"OK\",\n" +
                                            "  \"message\": \"블로그 조회 성공\",\n" +
                                            "  \"responseData\": \"블로그 데이터\"\n" +
                                            "}")
                    })
    )
    @Parameter(name = "size", description = "한번에 보여질 갯수")
    @Parameter(name = "cursor", description = "시작할 블로그 번호")
    @GetMapping
    public CustomSuccessResponse getAllBlogs(@RequestParam(defaultValue = "10") Integer size,
                                             @RequestParam(required = false) Integer cursor,
                                             @AuthenticationPrincipal String principal);

    @Operation(summary = "블로그 작성", description = "제목, 내용, 기록, 사진을 입력 받아 블로그 작성")
    @ApiResponse(responseCode = "201",description = "블로그 작성 성공",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "블로그 작성 성공 예",
                                    description = "블로그 작성에 성공하여 DB와 S3에 이미지 업로드가 성공하면 이미지 URL이 응답값으로 반환됩니다.",
                                    value = "{\n" +
                                            "  \"code\": 201,\n" +
                                            "  \"httpStatus\": \"OK\",\n" +
                                            "  \"message\": \"S3 업로드 성공\",\n" +
                                            "  \"responseData\": \"S3 이미지 URL\"\n" +
                                            "}")
                    })
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    CustomSuccessResponse writeBlog(@RequestBody BlogRequestDTO blogRequestDTO,
                                           @AuthenticationPrincipal String principal) throws Exception;

    @Operation(summary = "좋아요 기능", description = "좋아요 누르기, 취소")
    @ApiResponse(responseCode = "201",description = "좋아요를 눌렀습니다, 취소했습니다",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "좋아요 누르기 성공 예",
                                    description = "좋아요를 누르면 좋아요를 눌렀습니다 / 좋아요를 취소했습니다 응답값으로 반환됨",
                                    value = "{\n" +
                                            "  \"code\": 201,\n" +
                                            "  \"httpStatus\": \"OK\",\n" +
                                            "  \"message\": \"좋아요를 눌렀습니다, 좋아요를 취소했습니다.\",\n" +
                                            "  \"responseData\": \"null\"\n" +
                                            "}")
                    })
    )
    @PostMapping("/like")
    CustomSuccessResponse likeBlog(@RequestParam Integer blogId,
                    @AuthenticationPrincipal String principal) throws Exception;


    @PutMapping
    CustomSuccessResponse updateBlog(@RequestBody BlogRequestDTO blogRequestDTO,
                                     @RequestParam Integer blogId,
                                     @AuthenticationPrincipal String principal) throws IOException;

    @DeleteMapping
    CustomSuccessResponse deleteBlog(@AuthenticationPrincipal String principal,
                                     @RequestParam Integer blogId);
}
