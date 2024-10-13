package com.github.accountmanagementproject.repository.blogComment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogCommentRepository extends JpaRepository<BlogComment, Integer> {
}
