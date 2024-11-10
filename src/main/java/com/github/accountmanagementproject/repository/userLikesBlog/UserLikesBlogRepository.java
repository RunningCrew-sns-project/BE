package com.github.accountmanagementproject.repository.userLikesBlog;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.blog.Blog;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserLikesBlogRepository extends JpaRepository<UserLikesBlog, Integer> {

    @Query("SELECT  ulb FROM UserLikesBlog ulb " +
            "JOIN FETCH ulb.blog b " +
            "JOIN FETCH ulb.user u " +
            "WHERE u = :user AND b = :blog ")
    UserLikesBlog findByUserAndBlog(MyUser user, Blog blog);


//    List<UserLikesBlog> findByUser(MyUser user);

    @Query("SELECT ulb FROM UserLikesBlog ulb " +
            "JOIN FETCH ulb.user u " +
            "JOIN FETCH ulb.blog " +
            "WHERE u = :user ")
    List<UserLikesBlog> findByUser(@Param("user") MyUser user);

    List<UserLikesBlog> findAllByUser_UserId(Long userId);

    List<UserLikesBlog> findAllByUser(MyUser user);

    @Query("SELECT COUNT(ulb) FROM UserLikesBlog ulb WHERE ulb.blog = :blog ")
    Integer countAllByBlog(@Param("blog") Blog blog);
}
