package com.github.accountmanagementproject.web.controller.blog;

import com.github.accountmanagementproject.web.dto.blog.CommentRequestDTO;
import com.github.accountmanagementproject.web.dto.blog.CommentResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "CommentController", description = "댓글 작성, 조회, 수정, 삭제 API")
public interface CommentControllerDocs {
    @Operation(summary = "블로그 id를 받아 댓글 조회")
    @Parameter(name = "blogId", description = "댓글 조회할 블로그 아이디")
    @GetMapping
    List<CommentResponseDTO> getCommentsByBlogId(@RequestParam(name = "blogId") Integer blogId, @AuthenticationPrincipal String principal);

    @Operation(summary = "댓글 작성")
    @Parameters({
        @Parameter(name = "blogId", description = "댓글 작성할 블로그 아이디"),
        @Parameter(name = "comment", description = "댓글 작성 내용", example = "작성할 댓글 내용")
    })
    @PostMapping
    String createComment(@RequestParam(name = "blogId") Integer blogId, @RequestBody CommentRequestDTO comment,
                         @AuthenticationPrincipal String principal);

    @Operation(summary = "댓글 수정")
    @Parameters({
            @Parameter(name = "commentId", description = "수정할 댓글 아이디"),
            @Parameter(name = "comment", description = "수정할 댓글 내용")
    })
    @PutMapping
    String updateComment(@RequestParam(name = "commentId") Integer commentId, @RequestBody CommentRequestDTO comment,
                         @AuthenticationPrincipal String principal);

    @Operation(summary = "댓글 삭제")
    @Parameter(name = "commentId", description = "삭제할 댓글 아이디")
    @DeleteMapping
    String deleteComment(@RequestParam Integer commentId,
                         @AuthenticationPrincipal String principal);
}
