package com.github.accountmanagementproject.web.controller.blog;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.service.blog.BlogCommentService;
import com.github.accountmanagementproject.web.dto.blog.CommentRequestDTO;
import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController implements CommentControllerDocs{
    private final BlogCommentService blogCommentService;
    private final AccountConfig accountConfig;

    @Override
    @GetMapping
    public CustomSuccessResponse getCommentsByBlogId(@RequestParam(name = "blogId") Integer blogId,
                                                     @RequestParam(defaultValue = "10") Integer size,
                                                     @RequestParam(required = false) Integer cursor) {
        return new CustomSuccessResponse.SuccessDetail()
                .message("댓글을 조회했습니다.")
                .responseData(blogCommentService.getCommentByBlogId(blogId, size, cursor))
                .build();
    }

    @Override
    @PostMapping
    public CustomSuccessResponse createComment(@RequestParam(name = "blogId") Integer blogId, @RequestBody CommentRequestDTO comment,
                                               @AuthenticationPrincipal String principal) {
        MyUser user = accountConfig.findMyUser(principal);

        return new CustomSuccessResponse.SuccessDetail()
                .responseData(blogCommentService.createComment(blogId, comment, user))
                .message("댓글이 성공적으로 작성되었습니다.")
                .build();
    }

    @Override
    @PutMapping
    public CustomSuccessResponse updateComment(@RequestParam(name = "commentId") Integer commentId, @RequestBody CommentRequestDTO comment,
                                @AuthenticationPrincipal String principal){
        MyUser user = accountConfig.findMyUser(principal);
        return new CustomSuccessResponse.SuccessDetail()
                .responseData(blogCommentService.updateComment(commentId, comment, user))
                .message("댓글이 성공적으로 수정되었습니다.")
                .build();
    }

    @Override
    @DeleteMapping
    public CustomSuccessResponse deleteComment(@RequestParam Integer commentId,
                                @AuthenticationPrincipal String principal) {
        MyUser user = accountConfig.findMyUser(principal);
        return new CustomSuccessResponse.SuccessDetail()
                .message("댓글을 성공적으로 삭제했습니다.")
                .responseData(blogCommentService.deleteComment(commentId, user))
                .build();
    }
}
