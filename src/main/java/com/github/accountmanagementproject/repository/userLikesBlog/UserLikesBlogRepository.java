package com.github.accountmanagementproject.repository.userLikesBlog;

import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.blog.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLikesBlogRepository extends JpaRepository<UserLikesBlog, Integer> {
    UserLikesBlog findByUserAndBlog(MyUser user, Blog blog);
}
