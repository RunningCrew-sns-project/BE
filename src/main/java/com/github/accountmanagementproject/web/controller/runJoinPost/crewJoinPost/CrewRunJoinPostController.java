package com.github.accountmanagementproject.web.controller.runJoinPost.crewJoinPost;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.exception.SimpleRunAppException;
import com.github.accountmanagementproject.exception.enums.ErrorCode;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.repository.crew.crew.CrewsRepository;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPostRepository;
import com.github.accountmanagementproject.repository.runningPost.enums.ParticipationStatus;
import com.github.accountmanagementproject.service.runJoinPost.crewJoinPost.CrewJoinRunPostAlarmService;
import com.github.accountmanagementproject.service.runJoinPost.crewJoinPost.CrewJoinRunPostService;
import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
import com.github.accountmanagementproject.web.dto.responsebuilder.Response;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewParticipantsResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostCreateRequest;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostUpdateRequest;
import com.github.accountmanagementproject.web.dto.runJoinPost.crewRunGroup.CrewRunJoinResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.crewRunGroup.CrewRunJoinUpdateResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralParticipantsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final CrewJoinRunPostAlarmService alarmService;
    private final MyUsersRepository usersRepository;
    private final CrewsRepository crewsRepository;
    private final AccountConfig accountConfig;


    /** **************** "크루에 가입한 유저(crewId)" 또는 "크루마스터(crewMaster)"만 이용 가능 **************** */


    // 게시글 생성
    @PostMapping("/{crewId}/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
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


    // 상세 보기
    @GetMapping("/{runId}")
    @Override
    public Response<CrewRunPostResponse> getCrewPostByRunId(@PathVariable Long runId, @AuthenticationPrincipal String email) {
        MyUser user = accountConfig.findMyUser(email);
//        MyUser user = usersRepository.findByEmail(email)   //  TODO: 삭제 예정
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_DELETE, "User not found with email: " + email));

        CrewRunPostResponse crewRunPostResponse = crewJoinRunPostService.getPostById(runId, user);
        return Response.success(HttpStatus.OK, "크루 게시물이 정상 조회되었습니다.", crewRunPostResponse);
    }


    // 글 수정
    @PostMapping("/{crewId}/update/{runId}")
    @Override
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
    @Override
    public Response<Void> deleteCrewPost(@PathVariable Long crewId, @PathVariable Long runId,
                                         @AuthenticationPrincipal String email) {
        MyUser user = accountConfig.findMyUser(email);
//        MyUser user = usersRepository.findByEmail(email)
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_DELETE, "User not found with email: " + email));

        crewJoinRunPostService.deleteCrewPostByRunId(runId, user, crewId);
        return Response.success(HttpStatus.OK, "게시물이 정상 삭제되었습니다.", null);
    }


    // "크루 달리기 모집" 목록 가져오기
    @CrossOrigin(
            origins = {"http://localhost:8080", "http://54.180.9.220:8080"},
            allowedHeaders = "*",
            allowCredentials = "true"
    )
    @GetMapping("/list")
    @Override
    public Response<PageResponseDto<CrewRunPostResponse>> getAll(PageRequestDto pageRequestDto, @AuthenticationPrincipal String email) {
//                MyUser user = accountConfig.findMyUser(email);  // TODO: 수정 예정
        MyUser user = usersRepository.findByEmail(email)   //  TODO: 삭제 예정, 현재 로직에 맞춰 Not Found 가 아닌 것으로 대체함.
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.UNAUTHORIZED_CREW_VIEW));

        PageResponseDto<CrewRunPostResponse> response = crewJoinRunPostService.getAll(pageRequestDto, user);

        return Response.success(HttpStatus.OK, "모든 게시물이 조회되었습니다.", response);
    }


    // 참여 신청
    @PostMapping("join/{runId}")
    public Response<CrewRunJoinResponse> participateInCrewRun(
            @PathVariable Long runId,
            @RequestParam String email) {

        CrewRunJoinResponse response = alarmService.applyForCrewRun(runId, email);
        return Response.success(HttpStatus.OK, "처리가 완료되었습니다.", response);
    }

    // 2. 승인 또는 거절
    @PostMapping("/{runId}/approveOrReject/{userId}")
    public Response<CrewRunJoinUpdateResponse> approveParticipation(
            @PathVariable Long runId,
            @PathVariable Long userId,
            @RequestParam String principal) {

        try {
            CrewRunJoinUpdateResponse response = alarmService.processNewParticipation(runId, userId, principal);

            if (response.getStatus() == ParticipationStatus.REJECTED) {
                return Response.success(HttpStatus.OK, "강퇴 이력으로 인해 참여가 자동으로 거절되었습니다.", response);
            } else {
                return Response.success(HttpStatus.OK, "참여가 승인되었습니다.", response);
            }
        } catch (SimpleRunAppException e) {
            return Response.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), "참여 신청 처리 중 오류가 발생했습니다."
            );
        }
    }


    // 강퇴
    @PostMapping("/{runId}/kickout/{userId}")
    public Response<CrewRunJoinUpdateResponse> expelParticipant(
            @PathVariable Long runId,
            @PathVariable Long userId,
            @RequestParam String email) {
        CrewRunJoinUpdateResponse response = alarmService.forceToKickOut(runId, userId, email);
        return Response.success(HttpStatus.OK, "모임에서 강퇴되었습니다.", response);
    }

//    {
//        "resultCode": "success",
//            "code": 200,
//            "httpStatus": "OK",
//            "message": "모임에서 강퇴되었습니다.",
//            "detailMessage": null,
//            "responseData": {
//        "runId": 27,
//                "title": "42번크 달리기 ",
//                "adminId": 57,
//                "adminNickname": "이유경",
//                "userId": 11,
//                "nickname": "뭐얌",
//                "userEmail": "abc2@abc.com",
//                "status": "강제 퇴장",
//                "statusUpdatedAt": "2024-11-17 05:41:18",
//                "crewRunPost": true
//    },
//        "timestamp": "2024-11-17T05:41:18.4696627"
//    }


    @GetMapping("/participants/list/{runId}")
    public Response<List<CrewParticipantsResponse>> getAllParticipants(
//                                                                PageRequestDto pageRequestDto ,
            @PathVariable Long runId
//                                                                    @RequestParam String email
    ) {
//        MyUser user = accountConfig.findMyUser(email);  // TODO: 수정 예정
//        MyUser user = usersRepository.findByEmail(email)   //  TODO: 삭제 예정
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND, "User not found with email: " + email));
        List<CrewParticipantsResponse> result = alarmService.getAllParticipants(runId);
        return Response.success(HttpStatus.OK, "크루 참여자 리스트가 조회되었습니다.", result);
    }

//    {
//        "resultCode": "success",
//            "code": 200,
//            "httpStatus": "OK",
//            "message": "참여가 승인되었습니다.",
//            "detailMessage": null,
//            "responseData": {
//        "runId": 18,
//                "title": "한강크루 ",
//                "adminId": 57,
//                "adminNickname": "이유경",
//                "userId": 11,
//                "nickname": "뭐얌",
//                "userEmail": "abc2@abc.com",
//                "status": "가입 완료",
//                "statusUpdatedAt": "2024-11-17 04:45:33",
//                "crewRunPost": true
//    },
//        "timestamp": "2024-11-17T04:45:33.7850465"
//    }



//    {
//        "resultCode": "success",
//            "code": 200,
//            "httpStatus": "OK",
//            "message": "처리가 완료되었습니다.",
//            "detailMessage": null,
//            "responseData": {
//        "runId": 18,
//                "title": "한강크루 ",
//                "adminId": null,  // 승인이 되면 adminId와 adminNickname이 채워져서 응답
//                "adminNickname": null,  // 승인이 되면 adminId와 adminNickname이 채워져서 응답
//                "userId": 11,
//                "nickname": "뭐얌",
//                "userEmail": "abc2@abc.com",
//                "status": "가입 대기",
//                "requestedDate": "2024-11-17 02:59:56",
//                "crewRunPost": true
//    },
//        "timestamp": "2024-11-17T02:59:57.082428"
//    }








    // CrewJoinPost 목록과 참여 인원 수를 반환하는 API
    // Test 목적
//    @GetMapping("/crew-count")
//    public List<Map<String, Object>> getCrewPostsWithParticipantCount() {
//        return crewJoinPostRepository.findCrewPostsWithParticipantCount().stream().map(result -> {
//            CrewJoinPost post = (CrewJoinPost) result[0];
//            Long participantCount = (Long) result[1];
//            Map<String, Object> postInfo = new HashMap<>();
//            postInfo.put("postId", post.getCrewPostId());
//            postInfo.put("title", post.getTitle());
//            postInfo.put("participantCount", participantCount);
//            return postInfo;
//        }).collect(Collectors.toList());
//    }


}
