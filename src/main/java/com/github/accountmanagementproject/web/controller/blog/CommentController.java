package com.github.accountmanagementproject.web.controller.blog;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.service.blog.BlogCommentService;
import com.github.accountmanagementproject.web.dto.blog.BlogCommentRequestDTO;
import com.github.accountmanagementproject.web.dto.blog.BlogCommentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final BlogCommentService blogCommentService;
    private final AccountConfig accountConfig;

    @GetMapping
    public List<BlogCommentResponseDTO> getComments() {
        return blogCommentService.getAllComments();
    }

    @PostMapping
    public String createComment(@RequestBody BlogCommentRequestDTO comment,
                                @AuthenticationPrincipal String principal) {
        MyUser user = accountConfig.findMyUser(principal);
        blogCommentService.createComment(comment, user);
        return "댓글 작성 완료";
    }

    @PutMapping
    public String updateComment(@RequestBody BlogCommentRequestDTO comment,
                                @AuthenticationPrincipal String principal){
        MyUser user = accountConfig.findMyUser(principal);
        blogCommentService.updateComment(comment, user);
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
