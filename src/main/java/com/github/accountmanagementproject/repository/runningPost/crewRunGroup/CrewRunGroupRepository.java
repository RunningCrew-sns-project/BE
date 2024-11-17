package com.github.accountmanagementproject.repository.runningPost.crewRunGroup;

import com.github.accountmanagementproject.repository.runningPost.enums.ParticipationStatus;
import com.mongodb.RequestContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CrewRunGroupRepository extends JpaRepository<CrewRunGroup, CrewRunGroupId> {

    int countByUserUserIdAndStatus(Long userId, ParticipationStatus status);


    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM CrewRunGroup c " +
            "WHERE c.id.userId = :userId " +
            "AND c.id.crewPostId = :crewPostId")
    boolean existsByIdUserIdAndIdCrewPostId(@Param("userId") Long userId, @Param("crewPostId") Long crewPostId);


    @Query("SELECT cg FROM CrewRunGroup cg " +
            "JOIN FETCH cg.crewJoinPost cp " +
            "JOIN FETCH cp.crew c " +
            "JOIN FETCH c.crewMaster " +  // 크루장 정보가 필요한 경우
            "JOIN FETCH cg.user " +
            "LEFT JOIN FETCH cg.approver " +
            "WHERE cg.id.crewPostId = :crewPostId AND cg.id.userId = :userId")
    Optional<CrewRunGroup> findByIdWithDetails(@Param("crewPostId") Long crewPostId, @Param("userId") Long userId);



    @Query("SELECT cg FROM CrewRunGroup cg " +
            "JOIN FETCH cg.user u " +
            "LEFT JOIN FETCH cg.approver a " +
            "JOIN FETCH cg.crewJoinPost p " +
            "JOIN FETCH p.crew c " +
            "WHERE cg.id.crewPostId = :postId AND cg.status = 'APPROVED'" +
            "ORDER BY cg.statusUpdatedAt DESC")
    List<CrewRunGroup> findAllParticipantsByPostId(@Param("postId") Long postId);


    @Query("SELECT cg FROM CrewRunGroup cg " +
            "JOIN FETCH cg.user u " +
            "LEFT JOIN FETCH cg.approver a " +
            "JOIN FETCH cg.crewJoinPost p " +
            "JOIN FETCH p.crew c " +
            "WHERE cg.id.crewPostId = :postId " +
            "AND cg.status = :status " +
            "ORDER BY cg.statusUpdatedAt DESC")
    List<CrewRunGroup> findAllParticipantsByPostIdAndStatus(
            @Param("postId") Long postId,
            @Param("status") ParticipationStatus status
    );


}
