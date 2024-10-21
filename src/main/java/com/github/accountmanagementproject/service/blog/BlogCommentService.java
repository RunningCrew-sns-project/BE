package com.github.accountmanagementproject.service.blog;

import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.blog.Blog;
import com.github.accountmanagementproject.repository.blog.BlogRepository;
import com.github.accountmanagementproject.repository.blogComment.BlogComment;
import com.github.accountmanagementproject.repository.blogComment.BlogCommentRepository;
import com.github.accountmanagementproject.service.customExceptions.CustomBadCredentialsException;
import com.github.accountmanagementproject.service.mappers.CommentMapper;
import com.github.accountmanagementproject.web.dto.blog.BlogCommentRequestDTO;
import com.github.accountmanagementproject.web.dto.blog.BlogCommentResponseDTO;
import com.github.accountmanagementproject.web.dto.blog.PostCommentRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogCommentService {
    private final BlogCommentRepository blogCommentRepository;
    private final BlogRepository blogRepository;



    @Transactional
    public void createComment(PostCommentRequest comment, MyUser user) {
        Blog blog = blogRepository.findById(comment.getBlogId()).get();
        BlogComment blogComment = BlogComment.builder()
                .blog(blog)
                .content(comment.getContent())
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        blogCommentRepository.save(blogComment);
    }

    @Transactional
    public void updateComment(BlogCommentRequestDTO comment, MyUser user) {
        BlogComment blogComment = blogCommentRepository.findById(comment.getCommentId()).get();

        if(!user.equals(blogComment.getUser())) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        blogComment.setContent(comment.getContent());
        blogCommentRepository.save(blogComment);
    }

    public List<BlogCommentResponseDTO> getAllComments() {
        List<BlogComment> blogComments = blogCommentRepository.findAll();
        return blogComments
                .stream()
                .map(CommentMapper.INSTANCE::commentToCommentResponseDTO)
                .toList();
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
