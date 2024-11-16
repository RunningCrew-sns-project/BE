package com.github.accountmanagementproject.repository.runningPost.userRunGroups;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRunGroupRepository extends JpaRepository<UserRunGroup, UserRunGroupId> {

    @Query("SELECT ug FROM UserRunGroup ug " +
            "JOIN FETCH ug.id.user " +
            "JOIN FETCH ug.id.generalJoinPost " +
            "WHERE ug.id = :id")
    Optional<UserRunGroup> findByIdWithDetails(@Param("id") UserRunGroupId id);
}
