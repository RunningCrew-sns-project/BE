package com.github.accountmanagementproject.repository.crew_join_post.repository;


import com.github.accountmanagementproject.repository.crew_join_post.CrewJoinPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CrewJoinPostRepository extends JpaRepository<CrewJoinPost, Integer>, QuerydslPredicateExecutor<CrewJoinPost>, CrewJoinPostRepositoryCustom {
}
