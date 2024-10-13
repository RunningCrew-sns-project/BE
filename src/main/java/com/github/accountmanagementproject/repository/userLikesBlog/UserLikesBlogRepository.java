package com.github.accountmanagementproject.repository.userLikesBlog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLikesBlogRepository extends JpaRepository<UserLikesBlog, Integer> {
}
