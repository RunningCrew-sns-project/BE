package com.github.accountmanagementproject.repository.runningPost.generalPost;

import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GeneralJoinPostRepository extends JpaRepository<GeneralJoinPost, Long>, GeneralJoinPostRepositoryCustom {

    @Query("SELECT c FROM GeneralJoinPost c LEFT JOIN FETCH c.generalJoinPostImages WHERE c.runId = :runId")
    Optional<GeneralJoinPost> findByIdWithImages(@Param("runId") Long runId);
}
