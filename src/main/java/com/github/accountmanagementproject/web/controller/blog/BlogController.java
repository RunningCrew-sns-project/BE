package com.github.accountmanagementproject.web.controller.blog;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.blog.Blog;
import com.github.accountmanagementproject.service.blog.BlogService;
import com.github.accountmanagementproject.web.dto.blog.BlogDTO;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;
    private final AccountConfig accountConfig;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //블로그 작성
    public void writeBlog(
            @RequestPart(required = false, value = "image") MultipartFile item_image,
            @RequestPart BlogDTO blogDTO,
            @AuthenticationPrincipal String principal) {
        MyUser user = accountConfig.findMyUser(principal);
        blogService.writeBlog(blogDTO, user, item_image);
    }

    @PostMapping("/{blogId}/like") //블로그 좋아요
    public void likeBlog(@PathVariable Integer blogId, @AuthenticationPrincipal String principal) {
        System.out.println(principal);
        MyUser user = accountConfig.findMyUser(principal);
        blogService.likeBlog(blogId, user);
    }
}