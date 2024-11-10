package com.github.accountmanagementproject.web.controller.runJoinPost.crewJoinPost;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.exception.SimpleRunAppException;
import com.github.accountmanagementproject.exception.enums.ErrorCode;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.repository.crew.crew.CrewsRepository;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;

import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPostRepository;
import com.github.accountmanagementproject.service.runJoinPost.crewJoinPost.CrewJoinRunPostService;
import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
import com.github.accountmanagementproject.web.dto.responsebuilder.Response;

import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostCreateRequest;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostResponseMapper;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/join-posts/crews")
@CrossOrigin(originPatterns = "*")
public class CrewRunJoinPostController implements CrewRunJoinPostControllerDocs {
    // implements CrewRunJoinPostControllerDocs

    private final CrewJoinPostRepository crewJoinPostRepository;
    private final CrewJoinRunPostService crewJoinRunPostService;
    private final MyUsersRepository usersRepository;
    private final CrewsRepository crewsRepository;
    private final AccountConfig accountConfig;


    /** **************** "크루에 가입한 유저(crewId)" 또는 "크루마스터(crewMaster)"만 이용 가능 **************** */


    // 게시글 생성
    @PostMapping("/{crewId}/create")
    @ResponseStatus(HttpStatus.CREATED)
//    @Override
    public Response<CrewRunPostResponse> createCrewPost(
            @RequestBody @Valid CrewRunPostCreateRequest request,
            @PathVariable Long crewId,
            @AuthenticationPrincipal String email) {

        MyUser user = accountConfig.findMyUser(email);
//        MyUser user = usersRepository.findByEmail(email)   //  TODO: 삭제 예정
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_DELETE, "User not found with email: " + email));
        CrewRunPostResponse createdPost = crewJoinRunPostService.createCrewPost(request, user, crewId);
        return Response.success("크루 게시물이 생성되었습니다.", createdPost);
    }


    // 상세 보기 , crewPostSequence 로 조회
//    @PreAuthorize("@crewSecurityService.isUserInCrew(authentication, #crewId)") 수정 필요
//    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{runId}")
//    @Override
    public Response<CrewRunPostResponse> getCrewPostByRunId(@PathVariable Long runId, @AuthenticationPrincipal String email) {
        MyUser user = accountConfig.findMyUser(email);
//        MyUser user = usersRepository.findByEmail(email)   //  TODO: 삭제 예정
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_DELETE, "User not found with email: " + email));

        CrewRunPostResponse crewRunPostResponse = crewJoinRunPostService.getPostById(runId, user);
        return Response.success(HttpStatus.OK, "크루 게시물이 정상 조회되었습니다.", crewRunPostResponse);
    }


    // 글 수정
    @PostMapping("/{crewId}/update/{runId}")
//    @Override
    public Response<CrewRunPostResponse> updateCrewPost(@PathVariable Long crewId, @PathVariable Long runId,
                                                        @RequestBody @Valid CrewRunPostUpdateRequest request, @AuthenticationPrincipal String email) {
        MyUser user = accountConfig.findMyUser(email);
//        MyUser user = usersRepository.findByEmail(email)   //  TODO: 삭제 예정
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_DELETE, "User not found with email: " + email));

        CrewRunPostResponse updatedPost = crewJoinRunPostService.updateCrewPostByRunId(runId, crewId, user, request);
        return Response.success(HttpStatus.OK, "크루 게시물이 정상 수정되었습니다.", updatedPost);
    }


    // 게시글 삭제
    @DeleteMapping("/{crewId}/delete/{runId}")
//    @Override
    public Response<Void> deleteCrewPost(@PathVariable Long crewId, @PathVariable Long runId,
                                         @AuthenticationPrincipal String email) {
        MyUser user = accountConfig.findMyUser(email);
//        MyUser user = usersRepository.findByEmail(email)
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_DELETE, "User not found with email: " + email));

        crewJoinRunPostService.deleteCrewPostByRunId(runId, user, crewId);
        return Response.success(HttpStatus.OK, "게시물이 정상 삭제되었습니다.", null);
    }


    // "크루 달리기 모집" 목록 가져오기
//    @PreAuthorize("@crewSecurityService.isUserInCrew(authentication, #crewId)") 수정 필요
//    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public Response<PageResponseDto<CrewRunPostResponse>> getAll(PageRequestDto pageRequestDto, @RequestParam String email) {
//                MyUser user = accountConfig.findMyUser(email);  // TODO: 수정 예정
        MyUser user = usersRepository.findByEmail(email)   //  TODO: 삭제 예정, 현재 로직에 맞춰 Not Found 가 아닌 것으로 대체함.
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.UNAUTHORIZED_CREW_VIEW));

        PageResponseDto<CrewRunPostResponse> response = crewJoinRunPostService.getAll(pageRequestDto, user);

        return Response.success(HttpStatus.OK, "모든 게시물이 조회되었습니다.", response);
    }


    // CrewJoinPost 목록과 참여 인원 수를 반환하는 API
    // Test 목적
    @GetMapping("/crew-count")
    public List<Map<String, Object>> getCrewPostsWithParticipantCount() {
        return crewJoinPostRepository.findCrewPostsWithParticipantCount().stream().map(result -> {
            CrewJoinPost post = (CrewJoinPost) result[0];
            Long participantCount = (Long) result[1];
            Map<String, Object> postInfo = new HashMap<>();
            postInfo.put("postId", post.getCrewPostId());
            postInfo.put("title", post.getTitle());
            postInfo.put("participantCount", participantCount);
            return postInfo;
        }).collect(Collectors.toList());
    }


}
