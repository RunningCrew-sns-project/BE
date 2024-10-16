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

import java.io.IOException;

@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
public class BlogController implements BlogControllerDocs{
    private final BlogService blogService;
    private final AccountConfig accountConfig;

    //블로그 작성
    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CustomSuccessResponse writeBlog(@RequestPart(required = false, value = "image") MultipartFile image,
                                           @RequestPart BlogRequestDTO blogRequestDTO,
                                           @AuthenticationPrincipal String principal) throws Exception {
        MyUser user = accountConfig.findMyUser(principal);

        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.CREATED)
                .message("S3 업로드 성공")
                .responseData(blogService.writeBlog(blogRequestDTO, user, image))
                .build();
    }

    //블로그 좋아요
    @Override
    @PostMapping("/like")
    public String likeBlog(@RequestParam Integer blogId,
                           @AuthenticationPrincipal String principal) throws Exception {
        System.out.println(principal);
        MyUser user = accountConfig.findMyUser(principal);
        return blogService.likeBlog(blogId, user);

    }

    //블로그 조회
    //TODO : 내가 쓴 글만 보여지게 할지?
    @Override
    @GetMapping
    public CustomSuccessResponse getBlogs(@AuthenticationPrincipal String principal) {
        MyUser user = accountConfig.findMyUser(principal);
        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.OK)
                .message("모든 블로그를 조회했습니다.")
                .responseData(blogService.getBlogs(user))
                .build();
    }

    //블로그 수정
    @Override
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CustomSuccessResponse updateBlog(@RequestPart(required = false, value = "image") MultipartFile image,
                                            @RequestPart BlogRequestDTO blogRequestDTO,
                                            @RequestParam Integer blogId,
                                            @AuthenticationPrincipal String principal) throws IOException {
        MyUser user = accountConfig.findMyUser(principal);
        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.OK)
                .message("성공적으로 수정되었습니다.")
                .responseData(blogService.updateBlog(image,blogRequestDTO,blogId, user))
                .build();
    }

    //블로그 삭제
    @Override
    @DeleteMapping
    public CustomSuccessResponse deleteBlog(@AuthenticationPrincipal String principal,
                                            @RequestParam Integer blogId) {
        MyUser user = accountConfig.findMyUser(principal);
        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.OK)
                .message("성공적으로 삭제되었습니다.")
                .responseData(blogService.deleteBlog(blogId, user))
                .build();
    }


}