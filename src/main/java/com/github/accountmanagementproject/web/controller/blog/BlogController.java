package com.github.accountmanagementproject.web.controller.blog;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.service.blog.BlogService;
import com.github.accountmanagementproject.web.dto.blog.BlogRequestDTO;
import com.github.accountmanagementproject.web.dto.response.CustomSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;
    private final AccountConfig accountConfig;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //블로그 작성
    public CustomSuccessResponse writeBlog(
            @RequestPart(required = false, value = "image") MultipartFile image,
            @RequestPart BlogRequestDTO blogRequestDTO,
            @AuthenticationPrincipal String principal) throws Exception {
        MyUser user = accountConfig.findMyUser(principal);


        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.OK)
                .message("S3 업로드 성공")
                .responseData(blogService.writeBlog(blogRequestDTO, user, image))
                .build();
    }

    @PostMapping("/{blogId}/like") //블로그 좋아요
    public String likeBlog(@PathVariable Integer blogId, @AuthenticationPrincipal String principal) throws Exception {
        System.out.println(principal);
        MyUser user = accountConfig.findMyUser(principal);
        return blogService.likeBlog(blogId, user);

    }

    @GetMapping
    public CustomSuccessResponse getBlogs(@AuthenticationPrincipal String principal) {
        MyUser user = accountConfig.findMyUser(principal);
        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.OK)
                .message("모든 블로그를 조회했습니다.")
                .responseData(blogService.getBlogs(user))
                .build();
    }
}