package com.github.accountmanagementproject.repository.runningPost.crewPost;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface CrewJoinPostRepositoryCustom {

    List<CrewJoinPost> findFilteredPosts(LocalDate date, String location, Integer cursor, int size);
}
