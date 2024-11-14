package com.github.accountmanagementproject.service.blog;

import com.github.accountmanagementproject.exception.CustomBadCredentialsException;
import com.github.accountmanagementproject.exception.CustomNotFoundException;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.blog.Blog;
import com.github.accountmanagementproject.repository.blog.BlogRepository;
import com.github.accountmanagementproject.repository.blogComment.BlogComment;
import com.github.accountmanagementproject.repository.blogComment.BlogCommentRepository;
import com.github.accountmanagementproject.service.ExeTimer;
import com.github.accountmanagementproject.service.ScrollPaginationCollection;
import com.github.accountmanagementproject.service.mapper.comment.CommentMapper;
import com.github.accountmanagementproject.web.dto.blog.CommentRequestDTO;
import com.github.accountmanagementproject.web.dto.blog.CommentResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogCommentService {
    private final BlogCommentRepository blogCommentRepository;
    private final BlogRepository blogRepository;

    @ExeTimer
    public ScrollPaginationCollection<CommentResponseDTO> getCommentByBlogId(Integer blogId, Integer size, Integer cursor) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(null);

        Integer lastCommentId = (cursor != null) ? cursor : blogCommentRepository.findTopByBlogOrderByIdDesc(blog).orElseThrow(() -> new CustomNotFoundException.ExceptionBuilder()
                .customMessage("댓글이 없습니다.")
                .build()).getId();

        PageRequest pageRequest = PageRequest.of(0, size + 1);
        Page<BlogComment> blogCommentPage = blogCommentRepository.findByIdLessThanOrderByIdDesc(lastCommentId + 1, pageRequest);

        List<BlogComment> blogCommentList = blogCommentPage.getContent();

        List<CommentResponseDTO> responseList = blogCommentList
                .stream()
                .map(CommentMapper.INSTANCE::commentToCommentResponseDTO)
                .toList();

        boolean lastScroll = responseList.size() <= size;

        List<CommentResponseDTO> currentScrollItems = lastScroll ? responseList : responseList.subList(0, size);

        CommentResponseDTO nextCursor = lastScroll ? null : responseList.get(size);

        return ScrollPaginationCollection.of(currentScrollItems, size, lastScroll, nextCursor);
    }

    @ExeTimer
    @Transactional
    public BlogComment createComment(Integer blogId, CommentRequestDTO comment, MyUser user) {
        Blog blog = blogRepository.findById(blogId).orElse(null);
        BlogComment blogComment = BlogComment.builder()
                .blog(blog)
                .content(comment.getContent())
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        blogCommentRepository.save(blogComment);
        return blogComment;
    }

    @ExeTimer
    @Transactional
    public BlogComment updateComment(Integer commentId, CommentRequestDTO comment, MyUser user) {
        BlogComment blogComment = blogCommentRepository.findById(commentId).orElse(null);

        if(!user.equals(blogComment.getUser())) {
            throw new CustomBadCredentialsException.ExceptionBuilder()
                    .customMessage("권한이 없습니다")
                    .request("작성자 권한 필요")
                    .build();
        }

        blogComment.setContent(comment.getContent());
        blogCommentRepository.save(blogComment);

        return blogComment;
    }

    @ExeTimer
    public String deleteComment(Integer commentId, MyUser user) {
        BlogComment blogComment = blogCommentRepository.findById(commentId).get();
        if(!user.equals(blogComment.getUser())) {
            throw new CustomBadCredentialsException.ExceptionBuilder()
                    .customMessage("권한이 없습니다")
                    .request("작성자 권한 필요")
                    .build();
        }
        blogCommentRepository.delete(blogComment);

        return commentId + "를 삭제하였습니다.";
    }


}
