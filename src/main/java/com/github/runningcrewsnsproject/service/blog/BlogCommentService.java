package com.github.runningcrewsnsproject.service.blog;

import com.github.runningcrewsnsproject.exception.CustomBadCredentialsException;
import com.github.runningcrewsnsproject.repository.account.user.MyUser;
import com.github.runningcrewsnsproject.repository.blog.Blog;
import com.github.runningcrewsnsproject.repository.blog.BlogRepository;
import com.github.runningcrewsnsproject.repository.blogComment.BlogComment;
import com.github.runningcrewsnsproject.repository.blogComment.BlogCommentRepository;
import com.github.runningcrewsnsproject.web.dto.blog.CommentRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
