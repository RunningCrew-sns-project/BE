package com.github.accountmanagementproject.repository.blogComment;

import com.github.accountmanagementproject.repository.blog.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogCommentRepository extends JpaRepository<BlogComment, Integer> {
    List<BlogComment> findAllByBlog(Blog blog);
}
