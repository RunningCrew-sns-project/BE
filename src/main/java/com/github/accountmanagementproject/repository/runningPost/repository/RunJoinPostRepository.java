package com.github.accountmanagementproject.repository.runningPost.repository;

import com.github.accountmanagementproject.repository.runningPost.RunJoinPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RunJoinPostRepository extends JpaRepository<RunJoinPost, Integer>, QuerydslPredicateExecutor<RunJoinPost>, RunJoinPostRepositoryCustom {

    // sequence 증가하는 메서드 1
    @Query("SELECT COALESCE(MAX(r.crewPostSequence), 0) FROM RunJoinPost r WHERE r.crew.crewId = :crewId")
    Integer findMaxCrewPostSequenceByCrewId(@Param("crewId") Long crewId);

    // sequence 증가하는 메서드 2
//    @Query("SELECT COALESCE(MAX(r.generalPostSequence), 0) FROM RunJoinPost r WHERE r.crew.crewId = :crewId")
//    Integer findMaxGeneralPostSequenceByCrewId(@Param("crewId") Long crewId);

    // sequence 증가하는 메서드 3
    @Query("SELECT COALESCE(MAX(r.generalPostSequence), 0) FROM RunJoinPost r WHERE r.author.userId = :userId")
    Integer findMaxGeneralPostSequenceByUserId(@Param("userId") Long userId);

    // crewPostSequence 로 게시물 찾기
    @Query("SELECT r FROM RunJoinPost r WHERE r.crewPostSequence = :crewPostSequence")
    Optional<RunJoinPost> findByCrewPostSequence(@Param("crewPostSequence") Integer crewPostSequence);

    // generalPostSequence 로 게시물 찾기
    @Query("SELECT r FROM RunJoinPost r WHERE r.generalPostSequence = :generalPostSequence")
    Optional<RunJoinPost> findByGeneralPostSequence(Integer generalPostSequence);


}
