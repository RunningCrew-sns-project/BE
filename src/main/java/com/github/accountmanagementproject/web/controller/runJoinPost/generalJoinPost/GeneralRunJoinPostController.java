package com.github.accountmanagementproject.web.controller.runJoinPost.generalJoinPost;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.exception.SimpleRunAppException;
import com.github.accountmanagementproject.exception.enums.ErrorCode;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;

import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPost;
import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPostRepository;
import com.github.accountmanagementproject.service.runJoinPost.generalJoinPost.GeneralJoinRunPostService;

import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
import com.github.accountmanagementproject.web.dto.responsebuilder.Response;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostCreateRequest;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/join-posts/general")
public class GeneralRunJoinPostController {

    private final GeneralJoinPostRepository generalJoinPostRepository;
    private final GeneralJoinRunPostService generalRunJoinPostService;
    private final MyUsersRepository usersRepository;
    private final AccountConfig accountConfig;


    /** **************** "일반 User (크루 가입 X)" 또는 "크루"도 이용 가능************************************ */

    // 일반 User (크루 가입 X)
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<GeneralRunPostResponse> createGeneralPost(
            @RequestBody @Valid GeneralRunPostCreateRequest request, @RequestParam String email) {
//        MyUser user = accountConfig.findMyUser(principal);  // TODO: 수정 예정
        MyUser user = usersRepository.findByEmail(email)   //  TODO: 삭제 예정
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND, "User not found with email: " + email));

        GeneralJoinPost runJoinPost = generalRunJoinPostService.createGeneralPost(request, user);
        GeneralRunPostResponse responseDto = GeneralRunPostResponse.toDto(runJoinPost);
        return Response.success("게시물이 생성되었습니다.", responseDto);
    }


    // 게시글 상세보기
    @GetMapping("/{runId}")
    public Response<GeneralRunPostResponse> getPostById(@PathVariable Long runId) {
        GeneralJoinPost findPost = generalRunJoinPostService.getPostById(runId);
        GeneralRunPostResponse responseDto = GeneralRunPostResponse.toDto(findPost);
        return Response.success(HttpStatus.OK, "게시물이 정상 조회되었습니다.", responseDto);
    }


    // 게시글 수정
    @PostMapping("/update/{runId}")
    public Response<GeneralRunPostResponse> updatePostById(@PathVariable Long runId,
                                                                 @RequestBody @Valid GeneralRunPostUpdateRequest request,
                                                                 @RequestParam String email) {
//        MyUser user = accountConfig.findMyUser(principal); // TODO: 수정 예정
        MyUser user = usersRepository.findByEmail(email)   //  TODO: 삭제 예정
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND, "User not found with email: " + email));
        GeneralJoinPost updatedPost = generalRunJoinPostService.updateGeneralPost(runId, user, request);
        GeneralRunPostResponse responseDto = GeneralRunPostResponse.toDto(updatedPost);
        return Response.success(HttpStatus.OK, "게시물이 정상 수정되었습니다.", responseDto);
    }


    // 게시글 삭제
    @DeleteMapping("/delete/{runId}")
    public Response<Void> deletePostById(@PathVariable Long runId, @RequestParam String email) {
        //        MyUser user = accountConfig.findMyUser(principal);  // TODO: 수정 예정
        MyUser user = usersRepository.findByEmail(email)   //  TODO: 삭제 예정
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND, "User not found with email: " + email));
        generalRunJoinPostService.deleteGeneralPost(runId, user);
        return Response.success(HttpStatus.OK, "게시물이 정상 삭제되었습니다.", null);
    }


    // "일반 달리기 모집" 목록 가져오기
//    @PreAuthorize("@crewSecurityService.isUserInCrew(authentication, #crewId)") 수정 필요
//    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public Response<PageResponseDto<GeneralRunPostResponse>> getAll(PageRequestDto pageRequestDto) {
        PageResponseDto<GeneralRunPostResponse> response = generalRunJoinPostService.getAll(pageRequestDto);
        return Response.success(HttpStatus.OK, "모든 게시물이 조회되었습니다.", response);
    }


    // GeneralJoinPost 목록과 참여 인원 수를 반환하는 API
    // Test
    @GetMapping("/general-count")
    public List<Map<String, Object>> getGeneralPostsWithParticipantCount() {
        return generalJoinPostRepository.findGeneralPostsWithParticipantCount().stream().map(result -> {
            GeneralJoinPost post = (GeneralJoinPost) result[0];
            Long participantCount = (Long) result[1];
            Map<String, Object> postInfo = new HashMap<>();
            postInfo.put("postId", post.getGeneralPostId());
            postInfo.put("title", post.getTitle());
            postInfo.put("participantCount", participantCount);
            return postInfo;
        }).collect(Collectors.toList());
    }



}