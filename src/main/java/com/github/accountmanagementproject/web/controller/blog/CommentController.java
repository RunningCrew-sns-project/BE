package com.github.accountmanagementproject.web.controller.blog;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.service.blog.BlogCommentService;
import com.github.accountmanagementproject.web.dto.blog.CommentRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final BlogCommentService blogCommentService;
    private final AccountConfig accountConfig;

    @PostMapping("/{blogId}")
    public String createComment(@PathVariable(name = "blogId") Integer blogId,@RequestBody CommentRequestDTO comment,
                                @AuthenticationPrincipal String principal) {
        MyUser user = accountConfig.findMyUser(principal);
        blogCommentService.createComment(blogId, comment, user);
        return "댓글 작성 완료";
    }

    @PutMapping("{commentId}")
    public String updateComment(@PathVariable(name = "commentId") Integer commentId, @RequestBody CommentRequestDTO comment,
                                @AuthenticationPrincipal String principal){
        MyUser user = accountConfig.findMyUser(principal);
        blogCommentService.updateComment(commentId, comment, user);
        return "댓글 수정 완료";
    }

    @DeleteMapping
    public String deleteComment(@RequestParam Integer commentId,
                                @AuthenticationPrincipal String principal) {
        MyUser user = accountConfig.findMyUser(principal);
        blogCommentService.deleteComment(commentId, user);
        return "댓글 삭제 완료";
    }
}
