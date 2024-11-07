package com.github.accountmanagementproject.repository.runningPost.crewPost;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;


@Repository
public interface CrewJoinPostRepositoryCustom {

    Slice<CrewJoinPost> findFilteredPosts(LocalDate date, String location, Pageable pageable);
}
