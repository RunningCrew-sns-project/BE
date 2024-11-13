package com.github.accountmanagementproject.repository.blogComment;

import com.github.accountmanagementproject.repository.blog.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogCommentRepository extends JpaRepository<BlogComment, Integer> {
    List<BlogComment> findAllByBlog(Blog blog);
    BlogComment findTopByBlogOrderByIdDesc(Blog blog);
    Page<BlogComment> findByIdLessThanOrderByIdDesc(Integer lastCommentId, Pageable pageable);
}
