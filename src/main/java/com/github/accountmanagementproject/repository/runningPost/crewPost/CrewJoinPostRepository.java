package com.github.accountmanagementproject.repository.runningPost.crewPost;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CrewJoinPostRepository extends JpaRepository<CrewJoinPost, Long>, CrewJoinPostRepositoryCustom{


    @Query("SELECT c FROM CrewJoinPost c LEFT JOIN FETCH c.crewJoinPostImages WHERE c.runId = :runId")
    Optional<CrewJoinPost> findByIdWithImages(@Param("runId") Long runId);

    @Query("SELECT COUNT(c) FROM CrewJoinPost c")
    long countAll();

    @Query("SELECT c FROM CrewJoinPost c LEFT JOIN FETCH c.crewJoinPostImages")
    List<CrewJoinPost> findAllWithImages();


    // 조건에 맞는 CrewJoinPost ID만 페이징하여 조회
    @Query("SELECT c.runId FROM CrewJoinPost c " +
            "WHERE (:date IS NULL OR c.date = :date) " +
            "AND (:location IS NULL OR c.location = :location) " +
            "ORDER BY c.createdAt DESC")
    List<Long> findPostIdsByDateAndLocation(@Param("date") LocalDate date,
                                            @Param("location") String location,
                                            Pageable pageable);

    // 페치 조인으로 ID 리스트에 해당하는 게시물과 이미지 로드
    @Query("SELECT c FROM CrewJoinPost c LEFT JOIN FETCH c.crewJoinPostImages " +
            "WHERE c.runId IN :postIds " +
            "ORDER BY c.createdAt DESC")
    List<CrewJoinPost> findPostsWithImagesByIds(@Param("postIds") List<Long> postIds);
}
