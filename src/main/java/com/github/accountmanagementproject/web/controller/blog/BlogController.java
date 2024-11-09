package com.github.accountmanagementproject.web.controller.blog;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.service.blog.BlogService;
import com.github.accountmanagementproject.web.dto.blog.BlogRequestDTO;
import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class BlogController implements BlogControllerDocs{
    private final BlogService blogService;
    private final AccountConfig accountConfig;

    //블로그 조회
    //TODO : 내가 쓴 글만 보여지게 할지? 필터링 구현 필요할듯
    @Override
    @GetMapping
    public CustomSuccessResponse getAllBlogs(@RequestParam(defaultValue = "10") Integer size,
                                             @RequestParam(required = false) Integer cursor,
                                             @RequestParam Boolean isMyBlog,
                                             @AuthenticationPrincipal String principal) {

        System.out.println("Size: " + size + ", Cursor: " + cursor);

        MyUser user = accountConfig.findMyUser(principal);
        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.OK)
                .message("모든 블로그를 조회했습니다.")
                .responseData(blogService.getAllBlogs(size, cursor ,user, isMyBlog))
                .build();
    }

    @Override
    @GetMapping("/{blogId}")
    public CustomSuccessResponse getBlogById(@PathVariable Integer blogId, @AuthenticationPrincipal String principal) {
        MyUser user = accountConfig.findMyUser(principal);
        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.OK)
                .message("해당 블로그를 조회했습니다.")
                .responseData(blogService.getBlogById(blogId, user))
                .build();
    }

    //블로그 작성
    @Override
    @PostMapping
    public CustomSuccessResponse writeBlog(@RequestBody BlogRequestDTO blogRequestDTO,
                                           @AuthenticationPrincipal String principal) throws Exception {
        MyUser user = accountConfig.findMyUser(principal);

        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.CREATED)
                .message("블로그 작성 완료")
                .responseData(blogService.writeBlog(blogRequestDTO, user))
                .build();
    }

    //블로그 좋아요
    @Override
    @PostMapping("/like")
    public CustomSuccessResponse likeBlog(@RequestParam Integer blogId,
                           @AuthenticationPrincipal String principal) throws Exception {
        MyUser user = accountConfig.findMyUser(principal);
        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.OK)
                .message(blogService.likeBlog(blogId, user).get())
                .build();

    }

    //블로그 수정
    @Override
    @PutMapping
    public CustomSuccessResponse updateBlog(@RequestBody BlogRequestDTO blogRequestDTO,
                                            @RequestParam Integer blogId,
                                            @AuthenticationPrincipal String principal) throws IOException {
        MyUser user = accountConfig.findMyUser(principal);
        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.OK)
                .message("성공적으로 수정되었습니다.")
                .responseData(blogService.updateBlog(blogRequestDTO,blogId, user))
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