package com.github.accountmanagementproject.repository.blogComment;

import com.github.accountmanagementproject.repository.blog.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogCommentRepository extends JpaRepository<BlogComment, Integer> {
    List<BlogComment> findAllByBlog(Blog blog);
    Optional<BlogComment> findTopByBlogOrderByIdDesc(Blog blog);
    Page<BlogComment> findByIdLessThanOrderByIdDesc(Integer lastCommentId, Pageable pageable);
    Page<BlogComment> findByBlogAndIdLessThanOrderByIdDesc(Blog blog, Integer lastCommentId, Pageable pageable);
}
