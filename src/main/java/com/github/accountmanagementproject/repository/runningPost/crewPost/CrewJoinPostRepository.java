package com.github.accountmanagementproject.repository.runningPost.crewPost;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CrewJoinPostRepository extends JpaRepository<CrewJoinPost, Long>, CrewJoinPostRepositoryCustom{


    @Query("SELECT c FROM CrewJoinPost c LEFT JOIN FETCH c.crewJoinPostImages WHERE c.crewPostId = :crewPostId")
    Optional<CrewJoinPost> findByIdWithImages(@Param("crewPostId") Long crewPostId);

    @Query("SELECT COUNT(c) FROM CrewJoinPost c")
    long countAll();


//    @Query("SELECT c, COUNT(ur) as participantCount FROM CrewJoinPost c " +
//            "LEFT JOIN UserRunGroup ur ON c.crewPostId = ur.crewJoinPost.crewPostId " +
//            "GROUP BY c.crewPostId")
//    List<Object[]> findCrewPostsWithParticipantCount();

    @Query("SELECT c, COUNT(ur) as participantCount FROM CrewJoinPost c " +
            "LEFT JOIN FETCH c.participants ur " +
            "GROUP BY c.crewPostId")
    List<Object[]> findCrewPostsWithParticipantCount();

    List<CrewJoinPost> findAllByAuthor(MyUser user);
}
