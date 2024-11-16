package com.github.accountmanagementproject.repository.runningPost.runGroup;

import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPost;
import com.github.accountmanagementproject.repository.runningPost.userRunGroups.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RunGroupRepository extends JpaRepository<RunGroup, RunGroupId> {

    @Query("SELECT COUNT(rg) FROM RunGroup rg WHERE rg.generalJoinPost = :post AND rg.status = :status")
    int countByGeneralJoinPostAndStatus(
            @Param("post") GeneralJoinPost post,
            @Param("status") ParticipationStatus status
    );
}
