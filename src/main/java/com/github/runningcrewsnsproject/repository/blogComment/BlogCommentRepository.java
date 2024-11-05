package com.github.runningcrewsnsproject.repository.blogComment;

import com.github.runningcrewsnsproject.repository.blog.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogCommentRepository extends JpaRepository<BlogComment, Integer> {
    List<BlogComment> findAllByBlog(Blog blog);
}
