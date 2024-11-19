package com.github.accountmanagementproject.web.controller.runJoinPost.generalJoinPost;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPost;
import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPostRepository;
import com.github.accountmanagementproject.service.runJoinPost.generalJoinPost.GeneralJoinRunPostAlarmService;
import com.github.accountmanagementproject.service.runJoinPost.generalJoinPost.GeneralJoinRunPostService;
import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
import com.github.accountmanagementproject.web.dto.responsebuilder.Response;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralParticipantsResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostCreateRequest;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostUpdateRequest;
import com.github.accountmanagementproject.web.dto.runJoinPost.runGroup.GenRunJoinUpdateResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.runGroup.GeneralJoinResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/join-posts/general")
@CrossOrigin(originPatterns = "*")
public class GeneralRunJoinPostController implements GeneralRunJoinPostControllerDocs {
// implements GeneralRunJoinPostControllerDocs
    private final GeneralJoinPostRepository generalJoinPostRepository;
    private final GeneralJoinRunPostService generalRunJoinPostService;
    private final GeneralJoinRunPostAlarmService alarmService;
    private final MyUsersRepository usersRepository;
    private final AccountConfig accountConfig;


    /** **************** "일반 User (크루 가입 X)" 또는 "크루"도 이용 가능************************************ */

    // 일반 User (크루 가입 X)
    // 게시물 생성
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public Response<GeneralRunPostResponse> createGeneralPost(
            @RequestBody @Valid GeneralRunPostCreateRequest request,
            @AuthenticationPrincipal String email) {
        MyUser user = accountConfig.findMyUser(email);  // TODO: 수정 예정
//        MyUser user = usersRepository.findByEmail(email)   //  TODO: 삭제 예정
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND, "User not found with email: " + email));

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
    @Override
    public Response<GeneralRunPostResponse> updatePostById(@PathVariable Long runId,
                                                                 @RequestBody @Valid GeneralRunPostUpdateRequest request,
                                                           @AuthenticationPrincipal String email) {
        MyUser user = accountConfig.findMyUser(email); // TODO: 수정 예정
//        MyUser user = usersRepository.findByEmail(email)   //  TODO: 삭제 예정
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND, "User not found with email: " + email));
        GeneralJoinPost updatedPost = generalRunJoinPostService.updateGeneralPost(runId, user, request);
        GeneralRunPostResponse responseDto = GeneralRunPostResponse.toDto(updatedPost);
        return Response.success(HttpStatus.OK, "게시물이 정상 수정되었습니다.", responseDto);
    }


    // 게시글 삭제
    @DeleteMapping("/delete/{runId}")
    @Override
    public Response<Void> deletePostById(@PathVariable Long runId, @AuthenticationPrincipal String email) {
                MyUser user = accountConfig.findMyUser(email);  // TODO: 수정 예정
//        MyUser user = usersRepository.findByEmail(email)   //  TODO: 삭제 예정
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND, "User not found with email: " + email));
        generalRunJoinPostService.deleteGeneralPost(runId, user);
        return Response.success(HttpStatus.OK, "게시물이 정상 삭제되었습니다.", null);
    }


    // "일반 달리기 모집" 목록 가져오기
//    @PreAuthorize("@crewSecurityService.isUserInCrew(authentication, #crewId)") 수정 필요
//    @PreAuthorize("isAuthenticated()")
    @CrossOrigin(
            origins = {"http://localhost:8080", "http://54.180.9.220:8080"},
            allowedHeaders = "*",
            allowCredentials = "true"
    )
    @GetMapping("/list")
    public Response<PageResponseDto<GeneralRunPostResponse>> getAll(PageRequestDto pageRequestDto) {
        PageResponseDto<GeneralRunPostResponse> response = generalRunJoinPostService.getAll(pageRequestDto);
        return Response.success(HttpStatus.OK, "모든 게시물이 조회되었습니다.", response);
    }



    // 참여 신청
    @PostMapping("/join/{runId}")
    @Override
    public Response<GeneralJoinResponse> joinGeneralPost(
            @PathVariable Long runId,
            @AuthenticationPrincipal String email) {

        GeneralJoinResponse response = alarmService.applyToJoinPost(email, runId);
        return Response.success(HttpStatus.OK,"참여 신청이 완료되었습니다.", response);
    }

//    {  // TODO :  크루 - crewId, 일반 : runId , 신청자 : userId
//        "resultCode": "success",
//            "code": 200,
//            "httpStatus": "OK",
//            "message": "참여 신청이 완료되었습니다.",
//            "detailMessage": null,
//            "responseData": {
//        "nickname": "홍길동",
//                "userEmail": "abc@abc.com",
//                "status": "가입대기",
//                "requestedDate": "2024-11-14T18:30:07.705042200"
//    },
//        "timestamp": "2024-11-14T18:30:07.8073024"
//    }

    // 승인 또는 거절
    @PostMapping("/{runId}/approveOrReject/{userId}")
    @Override
    public Response<GenRunJoinUpdateResponse> approveOrReject(
            @PathVariable Long runId,
            @PathVariable Long userId,   // 신청자
            @AuthenticationPrincipal String email) {

        GenRunJoinUpdateResponse result = alarmService.processNewParticipation(runId, userId, email);
        return Response.success(HttpStatus.OK, "처리가 완료되었습니다.", result);
    }


    // 강퇴
    @PostMapping("/{runId}/kickout/{userId}")
    @Override
    public Response<GenRunJoinUpdateResponse> kickParticipant(
            @PathVariable Long runId,
            @PathVariable Long userId,
            @AuthenticationPrincipal String email
    ) {

        GenRunJoinUpdateResponse result = alarmService.forceToKickOut(email, runId, userId);

        return Response.success(HttpStatus.OK, "처리가 완료되었습니다.", result);
    }


    // general_post_id를 조회하면 해당 게시물의 모든 참여자들의 user_id, status , 참여일, 업데이트일 목록 조회
    @GetMapping("/participants/list/{runId}")
    @Override
    public Response<List<GeneralParticipantsResponse>> getAllParticipants(
//                                                                PageRequestDto pageRequestDto ,
                                                                    @PathVariable Long runId
//                                                                    @RequestParam String email
    ) {
//        MyUser user = accountConfig.findMyUser(email);  // TODO: 수정 예정
//        MyUser user = usersRepository.findByEmail(email)   //  TODO: 삭제 예정
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND, "User not found with email: " + email));
        List<GeneralParticipantsResponse> result = alarmService.getAllParticipants(runId);
        return Response.success(HttpStatus.OK, "일반 참여자 리스트가 조회되었습니다.", result);
    }


    // GeneralJoinPost 목록과 참여 인원 수를 반환하는 API
    // Test
//    @GetMapping("/general-count")
//    public List<Map<String, Object>> getGeneralPostsWithParticipantCount() {
//        return generalJoinPostRepository.findGeneralPostsWithParticipantCount().stream().map(result -> {
//            GeneralJoinPost post = (GeneralJoinPost) result[0];
//            Long participantCount = (Long) result[1];
//            Map<String, Object> postInfo = new HashMap<>();
//            postInfo.put("postId", post.getGeneralPostId());
//            postInfo.put("title", post.getTitle());
//            postInfo.put("participantCount", participantCount);
//            return postInfo;
//        }).collect(Collectors.toList());
//    }



}
