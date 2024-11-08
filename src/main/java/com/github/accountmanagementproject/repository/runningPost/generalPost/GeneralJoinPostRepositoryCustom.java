package com.github.accountmanagementproject.repository.runningPost.generalPost;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface GeneralJoinPostRepositoryCustom {

    List<GeneralJoinPost> findFilteredPosts(LocalDate date, String location, Integer cursor, int size);
}
