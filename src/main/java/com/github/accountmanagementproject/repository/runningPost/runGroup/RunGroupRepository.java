package com.github.accountmanagementproject.repository.runningPost.runGroup;

import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPost;
import com.github.accountmanagementproject.repository.runningPost.enums.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RunGroupRepository extends JpaRepository<RunGroup, RunGroupId> {

    @Query("SELECT COUNT(rg) FROM RunGroup rg WHERE rg.generalJoinPost = :post AND rg.status = :status")
    int countByGeneralJoinPostAndStatus(
            @Param("post") GeneralJoinPost post,
            @Param("status") ParticipationStatus status
    );


    @Query("SELECT rg FROM RunGroup rg " +
            "JOIN FETCH rg.user " +
            "LEFT JOIN FETCH rg.approver " +
            "WHERE rg.generalJoinPost.generalPostId = :postId AND rg.status = 'APPROVED' ")
    List<RunGroup> findAllParticipantsByPostId(@Param("postId") Long postId);


    @Query("SELECT rg FROM RunGroup rg " +
            "JOIN FETCH rg.user " +
            "LEFT JOIN FETCH rg.approver " +
            "WHERE rg.generalJoinPost.generalPostId = :postId AND rg.status = 'PENDING' ")
    List<RunGroup> getAllPendingParticipants(@Param("postId") Long postId);


    @Query("SELECT COUNT(r) FROM RunGroup r WHERE r.id.userId = :userId AND r.status = :status")
    int countByUserUserIdAndStatus(@Param("userId") Long userId, @Param("status") ParticipationStatus status);


    @Query(value = "SELECT COUNT(*) FROM run_group WHERE user_id = :userId AND status = :status", nativeQuery = true)
    int countByUserIdAndStatusNative(@Param("userId") Long userId, @Param("status") String status);
}
