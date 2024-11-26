package com.github.accountmanagementproject.service.runJoinPost;

import com.github.accountmanagementproject.exception.CustomAccessDenied;
import com.github.accountmanagementproject.exception.CustomBadRequestException;
import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPostRepository;
import com.github.accountmanagementproject.web.dto.runJoinPost.RunPostAndMemberResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RunPostService {
    private final GeneralJoinPostRepository generalJoinPostRepository;

    public List<RunPostAndMemberResponse> getRunPostUsers(boolean isCrew, String email, Boolean isAll) {
        return generalJoinPostRepository.findPostAndMembers(email, isCrew, isAll);
    }

    @Transactional
    public void runFromMemberDrop(Long postId, Long badUserId, String authorEmail, boolean isCrew) {
        boolean isPostAuthor = generalJoinPostRepository.isPostAuthor(postId, authorEmail, isCrew);
        if (!isPostAuthor) throw new CustomAccessDenied.ExceptionBuilder()
                .customMessage("달리기 게시물 작성자만 강퇴 할 수 있습니다.")
                .request(Map.of("isCrewRun",isCrew,"postId", postId,"authorEmail",authorEmail)).build();
        boolean result = generalJoinPostRepository.deleteMember(postId, badUserId, isCrew);
        if(!result) throw new CustomBadRequestException.ExceptionBuilder()
                .customMessage("강퇴 실패. 게시물을 찾을 수 없거나, 게시물에 승인된 해당 userId가 존재하지 않습니다.")
                .request(Map.of("isCrewRun",isCrew,"postId", postId,"badUserId",badUserId)).build();

    }
}
