package com.github.accountmanagementproject.web.controller.blog;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.service.blog.BlogCommentService;
import com.github.accountmanagementproject.web.dto.blog.CommentRequestDTO;
import com.github.accountmanagementproject.web.dto.blog.CommentResponseDTO;
import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController implements CommentControllerDocs{
    private final BlogCommentService blogCommentService;
    private final AccountConfig accountConfig;

    @Override
    @GetMapping
    public List<CommentResponseDTO> getCommentsByBlogId(@RequestParam(name = "blogId") Integer blogId, @AuthenticationPrincipal String principal) {
        MyUser user = accountConfig.findMyUser(principal);
        return blogCommentService.getCommentByBlogId(blogId, user);
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
    public String deleteComment(@RequestParam Integer commentId,
                                @AuthenticationPrincipal String principal) {
        MyUser user = accountConfig.findMyUser(principal);
        blogCommentService.deleteComment(commentId, user);
        return "댓글 삭제 완료";
    }
}
