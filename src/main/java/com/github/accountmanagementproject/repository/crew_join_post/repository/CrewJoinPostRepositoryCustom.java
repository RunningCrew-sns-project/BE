package com.github.accountmanagementproject.repository.crew_join_post.repository;


import com.github.accountmanagementproject.repository.crew_join_post.CrewJoinPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;


public interface CrewJoinPostRepositoryCustom {

    Slice<CrewJoinPost> findByLocation(String location, Pageable pageable);
}
