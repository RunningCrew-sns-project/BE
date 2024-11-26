package com.github.accountmanagementproject.repository.runningPost.generalPost;

import com.github.accountmanagementproject.web.dto.runJoinPost.RunPostAndMemberResponse;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface GeneralJoinPostRepositoryCustom {

    List<GeneralJoinPost> findFilteredPosts(LocalDate date, String location, Integer cursor, int size, String sortType);

    List<RunPostAndMemberResponse> findPostAndMembers(String email, boolean isCrew, Boolean isAll);

    boolean isPostAuthor(Long postId, String authorEmail, boolean isCrew);

    boolean deleteMember(Long postId, Long badUserId, boolean isCrew);
}
