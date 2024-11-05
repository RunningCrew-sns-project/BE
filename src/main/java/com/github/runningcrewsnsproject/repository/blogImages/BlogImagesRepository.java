package com.github.runningcrewsnsproject.repository.blogImages;


import com.github.runningcrewsnsproject.repository.blog.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogImagesRepository extends JpaRepository<BlogImages, Integer> {
    List<BlogImages> findAllByBlog(Blog blog);
    void deleteAllByBlog(Blog blog);
}
