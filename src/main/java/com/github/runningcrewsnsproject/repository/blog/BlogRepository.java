package com.github.runningcrewsnsproject.repository.blog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {
    @Modifying
    @Query("UPDATE Blog b SET b.likeCount = b.likeCount + 1 WHERE b.id = :blogId")
    void incrementLikeCount(@Param("blogId") Integer blogId);

    @Modifying
    @Query("UPDATE Blog b SET b.likeCount = CASE WHEN b.likeCount > 0 THEN b.likeCount - 1 ELSE 0 END WHERE b.id = :blogId")
    void decrementLikeCount(@Param("blogId") Integer blogId);

    Page<Blog> findByIdLessThanOrderByIdDesc(Integer id, Pageable pageable);

    Blog findTopByOrderByIdDesc();
}
