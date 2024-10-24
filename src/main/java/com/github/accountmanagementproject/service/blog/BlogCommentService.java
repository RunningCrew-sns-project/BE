package com.github.accountmanagementproject.service.blog;

import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.blog.Blog;
import com.github.accountmanagementproject.repository.blog.BlogRepository;
import com.github.accountmanagementproject.repository.blogComment.BlogComment;
import com.github.accountmanagementproject.repository.blogComment.BlogCommentRepository;
import com.github.accountmanagementproject.service.customExceptions.CustomBadCredentialsException;
import com.github.accountmanagementproject.service.mappers.comment.CommentMapper;
import com.github.accountmanagementproject.web.dto.blog.CommentRequestDTO;
import com.github.accountmanagementproject.web.dto.blog.CommentResponseDTO;
import com.github.accountmanagementproject.web.dto.blog.PostCommentRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogCommentService {
    private final BlogCommentRepository blogCommentRepository;
    private final BlogRepository blogRepository;

    @Transactional
    public void createComment(Integer blogId, CommentRequestDTO comment, MyUser user) {
        Blog blog = blogRepository.findById(blogId).orElse(null);
        BlogComment blogComment = BlogComment.builder()
                .blog(blog)
                .content(comment.getContent())
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        blogCommentRepository.save(blogComment);
    }

    @Transactional
    public void updateComment(Integer commentId, CommentRequestDTO comment, MyUser user) {
        BlogComment blogComment = blogCommentRepository.findById(commentId).orElse(null);

        if(!user.equals(blogComment.getUser())) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        blogComment.setContent(comment.getContent());
        blogCommentRepository.save(blogComment);
    }

    public void deleteComment(Integer commentId, MyUser user) {
        BlogComment blogComment = blogCommentRepository.findById(commentId).get();
        if(!user.equals(blogComment.getUser())) {
            throw new CustomBadCredentialsException.ExceptionBuilder()
                    .customMessage("권한이 없습니다")
                    .request("작성자 권한 필요")
                    .build();
        }
        blogCommentRepository.delete(blogComment);
    }
}
