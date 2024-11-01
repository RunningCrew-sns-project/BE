package com.github.accountmanagementproject.repository.runningPost.repository;

import com.github.accountmanagementproject.repository.runningPost.RunJoinPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.util.List;

public interface RunJoinPostRepositoryCustom {

    Slice<RunJoinPost> findPosts(Long crewId, LocalDate date, String location, Pageable pageable);


}
