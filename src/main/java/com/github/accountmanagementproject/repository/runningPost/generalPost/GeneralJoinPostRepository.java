package com.github.accountmanagementproject.repository.runningPost.generalPost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GeneralJoinPostRepository extends JpaRepository<GeneralJoinPost, Long>, GeneralJoinPostRepositoryCustom {

    @Query("SELECT c FROM GeneralJoinPost c LEFT JOIN FETCH c.generalJoinPostImages WHERE c.generalPostId = :generalPostId")
    Optional<GeneralJoinPost> findByIdWithImages(@Param("generalPostId") Long generalPostId);


//    @Query("SELECT g, COUNT(ur) as participantCount FROM GeneralJoinPost g " +
//            "LEFT JOIN UserRunGroup ur ON g.generalPostId = ur.generalJoinPost.generalPostId " +
//            "GROUP BY g.generalPostId")
//    List<Object[]> findGeneralPostsWithParticipantCount();

    @Query("SELECT g, COUNT(ur) as participantCount FROM GeneralJoinPost g " +
            "LEFT JOIN FETCH g.participants ur " +
            "GROUP BY g.generalPostId")
    List<Object[]> findGeneralPostsWithParticipantCount();


    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM GeneralJoinPost p " +
            "WHERE p.generalPostId = :postId AND p.author.email = :email")
    boolean isPostAuthor(@Param("email") String email, @Param("postId") Long postId);
}
