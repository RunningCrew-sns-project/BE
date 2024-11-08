package com.github.accountmanagementproject.repository.runningPost.generalPost;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;


@Repository
public interface GeneralJoinPostRepositoryCustom {

    Slice<GeneralJoinPost> findFilteredPosts(LocalDate date, String location, Pageable pageable);
}
