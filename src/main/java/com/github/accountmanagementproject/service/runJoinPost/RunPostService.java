package com.github.accountmanagementproject.service.runJoinPost;

import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPostRepository;
import com.github.accountmanagementproject.web.dto.runJoinPost.RunPostAndMemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RunPostService {
    private final GeneralJoinPostRepository generalJoinPostRepository;

    public List<RunPostAndMemberResponse> getRunPostUsers(boolean isCrew, String email, Boolean isAll) {
        return generalJoinPostRepository.findPostAndMembers(email, isCrew, isAll);
    }
}
