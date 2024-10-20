package com.github.accountmanagementproject.web.controller.blog;

import com.github.accountmanagementproject.web.dto.blog.BlogRequestDTO;
import com.github.accountmanagementproject.web.dto.responseSystem.CustomSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Blog", description = "블로그 작성, 좋아요, 댓글 관련 API")
public interface BlogControllerDocs {

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
    CustomSuccessResponse writeBlog(
            @RequestPart(required = false, value = "image") MultipartFile image,
            @RequestPart BlogRequestDTO blogRequestDTO,
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
    String likeBlog(@RequestParam Integer blogId,
                    @AuthenticationPrincipal String principal) throws Exception;

    @GetMapping
    CustomSuccessResponse getBlogs(@AuthenticationPrincipal String principal);

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    CustomSuccessResponse updateBlog(@RequestPart(required = false, value = "image") MultipartFile image,
                                     @RequestPart BlogRequestDTO blogRequestDTO,
                                     @RequestParam Integer blogId,
                                     @AuthenticationPrincipal String principal) throws IOException;

    @DeleteMapping
    CustomSuccessResponse deleteBlog(@AuthenticationPrincipal String principal,
                                     @RequestParam Integer blogId);
}
