package com.github.accountmanagementproject.web.controller.runJoinPost.generalJoinPost;

import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
import com.github.accountmanagementproject.web.dto.responsebuilder.Response;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralParticipantsResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostCreateRequest;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostUpdateRequest;
import com.github.accountmanagementproject.web.dto.runJoinPost.runGroup.GenRunJoinUpdateResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.runGroup.GeneralJoinResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Tag(name = "General Run Join Post", description = "일반 유저 및 크루 회원이 이용 가능한 일반 달리기 참여 게시물 관련 API")
public interface GeneralRunJoinPostControllerDocs {

    @Operation(summary = "게시글 생성", description = "새로운 일반 달리기 게시글을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "게시글 생성 성공",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "게시글 생성 성공 예시", value = """
                            {
                              "resultCode": "success",
                              "code": 201,
                              "httpStatus": "CREATED",
                              "message": "게시물이 생성되었습니다.",
                              "responseData": {
                                "runId": 1,
                                "authorId": 6,
                                "title": "주말 달리기 모집",
                                "content": "함께 달리기 하실 분을 모집합니다!",
                                "maximumPeople": 10,
                                "people": 0,
                                "location": "서울 강남구",
                                "date": "2023-11-10",
                                "startTime": null,
                                "status": "OPEN",
                                "postType": "GENERAL",
                                "inputLocation": "강남역",
                                "inputLatitude": 37.4979,
                                "inputLongitude": 127.0276,
                                "targetLocation": "선릉역",
                                "targetLatitude": 37.5045,
                                "targetLongitude": 127.0498,
                                "distance": 2.091375914267994,
                                "createdAt": "2024-11-07T05:34:33",
                                "updatedAt": null,
                                "banners": []
                              }
                            }""")
            )
    )
    Response<GeneralRunPostResponse> createGeneralPost(
            @Parameter(description = "일반 게시글 생성 요청 정보") GeneralRunPostCreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal String email);

    @Operation(summary = "게시글 상세 조회", description = "주어진 runId에 해당하는 일반 달리기 게시글을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 조회 성공",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "게시글 상세 조회 성공 예시", value = """
                            {
                              "resultCode": "success",
                              "code": 200,
                              "httpStatus": "OK",
                              "message": "게시물이 정상 조회되었습니다.",
                              "responseData": {
                                "runId": 1,
                                "authorId": 6,
                                "title": "주말 달리기 모집",
                                "content": "함께 달리기 하실 분을 모집합니다!",
                                "maximumPeople": 10,
                                "people": 0,
                                "location": "서울 강남구",
                                "date": "2023-11-10",
                                "startTime": null,
                                "status": "OPEN",
                                "postType": "GENERAL",
                                "inputLocation": "강남역",
                                "inputLatitude": 37.4979,
                                "inputLongitude": 127.0276,
                                "targetLocation": "선릉역",
                                "targetLatitude": 37.5045,
                                "targetLongitude": 127.0498,
                                "distance": 2.091375914267994,
                                "createdAt": "2024-11-07T05:34:33",
                                "updatedAt": null,
                                "banners": []
                              }
                            }""")
            )
    )
    Response<GeneralRunPostResponse> getPostById(
            @Parameter(description = "게시글 ID") Long runId);

    @Operation(summary = "게시글 수정", description = "기존의 일반 달리기 게시글을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 수정 성공",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "게시글 수정 성공 예시", value = """
                            {
                              "resultCode": "success",
                              "code": 200,
                              "httpStatus": "OK",
                              "message": "게시물이 정상 수정되었습니다.",
                              "responseData": {
                                "runId": 1,
                                "authorId": 6,
                                "title": "수정된 주말 달리기 모집",
                                "content": "수정된 모집 내용입니다.",
                                "maximumPeople": 10,
                                "people": 0,
                                "location": "서울 강남구",
                                "date": "2023-11-10",
                                "startTime": null,
                                "status": "OPEN",
                                "postType": "GENERAL",
                                "inputLocation": "강남역",
                                "inputLatitude": 37.4979,
                                "inputLongitude": 127.0276,
                                "targetLocation": "선릉역",
                                "targetLatitude": 37.5045,
                                "targetLongitude": 127.0498,
                                "distance": 2.091375914267994,
                                "createdAt": "2024-11-07T05:34:33",
                                "updatedAt": "2024-11-07T06:00:33",
                                "banners": []
                              }
                            }""")
            )
    )
    Response<GeneralRunPostResponse> updatePostById(
            @Parameter(description = "게시글 ID") Long runId,
            @Parameter(description = "수정할 게시글 정보") GeneralRunPostUpdateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal String principal);

    @Operation(summary = "게시글 삭제", description = "기존의 일반 달리기 게시글을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 삭제 성공",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "게시글 삭제 성공 예시", value = """
                            {
                              "resultCode": "success",
                              "code": 200,
                              "httpStatus": "OK",
                              "message": "게시물이 정상 삭제되었습니다.",
                              "responseData": null
                            }""")
            )
    )
    Response<Void> deletePostById(
            @Parameter(description = "게시글 ID *") Long runId,
            @Parameter(hidden = true) @AuthenticationPrincipal String principal);

    @Operation(summary = "일반 달리기 모집 게시물 목록 조회", description = "일반 달리기 모집 게시물을 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "게시물 목록 조회 성공",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "게시물 목록 조회 성공 예시", value = """
                            {
                              "resultCode": "success",
                              "code": 200,
                              "httpStatus": "OK",
                              "message": "모든 게시물이 조회되었습니다.",
                              "responseData": {
                                "content": [
                                  {
                                    "runId": 1,
                                    "authorId": 6,
                                    "title": "주말 달리기 모집",
                                    "content": "함께 달리기 하실 분을 모집합니다!",
                                    "maximumPeople": 10,
                                    "people": 0,
                                    "location": "서울 강남구",
                                    "date": "2023-11-10",
                                    "startTime": null,
                                    "status": "OPEN",
                                    "postType": "GENERAL",
                                    "inputLocation": "강남역",
                                    "inputLatitude": 37.4979,
                                    "inputLongitude": 127.0276,
                                    "targetLocation": "선릉역",
                                    "targetLatitude": 37.5045,
                                    "targetLongitude": 127.0498,
                                    "distance": 2.091375914267994,
                                    "createdAt": "2024-11-07T05:34:33",
                                    "updatedAt": null,
                                    "banners": []
                                  }
                                ],
                                "countPerScroll": 9,
                                "lastScroll": true,
                                "nextCursor": null
                              }
                            }""")
            )
    )
    Response<PageResponseDto<GeneralRunPostResponse>> getAll(
            @Parameter(description = "페이지 요청 정보") PageRequestDto pageRequestDto);


    /************************************************************************************************************/

    @Operation(summary = "일반 게시물 참여 신청", description = "사용자가 특정 일반 게시물에 참여 신청을 합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "참여 신청 성공",
                    content = @Content(
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(
                                    name = "참여 신청 성공 응답",
                                    value = """
                                            {
                                                "resultCode": "success",
                                                "code": 200,
                                                "httpStatus": "OK",
                                                "message": "참여 신청이 완료되었습니다.",
                                                "detailMessage": null,
                                                "responseData": {
                                                    "runId": 42,
                                                    "isCrewRunPost": false,
                                                    "title": "일반 달리기 게시물",
                                                    "adminId": 15,
                                                    "adminNickname": "관리자 닉네임",
                                                    "userId": 8,
                                                    "nickname": "참여자 닉네임",
                                                    "userEmail": "participant@example.com",
                                                    "status": "PENDING",
                                                    "requestedDate": "2024-11-17T12:30:45"
                                                },
                                                "timestamp": "2024-11-17T12:30:45.123456"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @PostMapping("/join/{runId}")
    public Response<GeneralJoinResponse> joinGeneralPost(
            @Parameter(description = "일반 게시물 ID", required = true)  Long runId,
            @Parameter(description = "사용자 이메일")  String email);

    @Operation(summary = "참여 승인 또는 거절", description = "관리자가 특정 사용자의 일반 게시물 참여를 승인 또는 거절합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "승인 또는 거절 처리 성공",
                    content = @Content(
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(
                                    name = "승인 또는 거절 성공 응답",
                                    value = """
                                            {
                                                "resultCode": "success",
                                                "code": 200,
                                                "httpStatus": "OK",
                                                "message": "처리가 완료되었습니다.",
                                                "detailMessage": null,
                                                "responseData": {
                                                    "runId": 42,
                                                    "title": "일반 달리기 게시물",
                                                    "adminId": 15,
                                                    "adminNickname": "관리자 닉네임",
                                                    "userId": 8,
                                                    "nickname": "참여자 닉네임",
                                                    "userEmail": "participant@example.com",
                                                    "status": "APPROVED",
                                                    "statusUpdatedAt": "2024-11-17T12:45:30"
                                                },
                                                "timestamp": "2024-11-17T12:45:30.123456"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @PostMapping("/{runId}/approveOrReject/{userId}")
    public Response<GenRunJoinUpdateResponse> approveOrReject(
            @Parameter(description = "일반 게시물 ID", required = true) @PathVariable Long runId,
            @Parameter(description = "참여 신청자 ID", required = true) @PathVariable Long userId,
            @Parameter(description = "관리자 이메일", required = false) @RequestParam(required = false) String email);

    @Operation(summary = "참여자 강퇴", description = "관리자가 특정 사용자를 일반 게시물에서 강퇴합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "강퇴 성공",
                    content = @Content(
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(
                                    name = "강퇴 성공 응답",
                                    value = """
                                            {
                                                "resultCode": "success",
                                                "code": 200,
                                                "httpStatus": "OK",
                                                "message": "처리가 완료되었습니다.",
                                                "detailMessage": null,
                                                "responseData": {
                                                    "runId": 42,
                                                    "title": "일반 달리기 게시물",
                                                    "adminId": 15,
                                                    "adminNickname": "관리자 닉네임",
                                                    "userId": 8,
                                                    "nickname": "참여자 닉네임",
                                                    "userEmail": "participant@example.com",
                                                    "status": "FORCED_EXIT",
                                                    "statusUpdatedAt": "2024-11-17T13:00:00"
                                                },
                                                "timestamp": "2024-11-17T13:00:00.123456"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @PostMapping("/{runId}/kickout/{userId}")
    public Response<GenRunJoinUpdateResponse> kickParticipant(
            @Parameter(description = "일반 게시물 ID", required = true) @PathVariable Long runId,
            @Parameter(description = "강퇴할 사용자 ID", required = true) @PathVariable Long userId,
            @Parameter(description = "관리자 이메일", required = true) @RequestParam String email);

    @Operation(summary = "참여 신청 후 -> '승인(APPROVED)' 유저 리스트 조회", description = "특정 일반 게시물의 '승인(APPROVED)'된 유저를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "'승인(APPROVED)' 리스트 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(
                                    name = "참여자 리스트 조회 성공 응답",
                                    value = """
                                            {
                                                "resultCode": "success",
                                                "code": 200,
                                                "httpStatus": "OK",
                                                "message": "'승인(APPROVED)' 리스트가 조회되었습니다.",
                                                "detailMessage": null,
                                                "responseData": [
                                                    {
                                                        "userId": 1,
                                                        "nickname": "참여자1",
                                                        "email": "user1@example.com",
                                                        "status": "APPROVED",
                                                        "joinedAt": "2024-11-17T12:15:00"
                                                    },
                                                    {
                                                        "userId": 2,
                                                        "nickname": "참여자2",
                                                        "email": "user2@example.com",
                                                        "status": "PENDING",
                                                        "joinedAt": "2024-11-17T12:20:00"
                                                    }
                                                ],
                                                "timestamp": "2024-11-17T13:00:00.123456"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/participants/approve-list/{runId}")
    public Response<List<GeneralParticipantsResponse>> getAllParticipants(
            @Parameter(description = "일반 게시물 ID", required = true) @PathVariable Long runId);


    @Operation(summary = "참여 신청 후 -> '참여 대기(PENDING)' 유저 리스트 조회", description = "특정 일반 게시물의 대기 상태인 유저를 조회합니다. adminId 가 null로 응답됩니다. 이후 승인 또는 거절 시 adminId 가 표시됨")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "참여자 리스트 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(
                                    name = "참여자 - 대기(PENDING) 리스트 조회 성공 응답",
                                    value = """
                                            {
                                                "resultCode": "success",
                                                "code": 200,
                                                "httpStatus": "OK",
                                                "message": "'참여 대기(PENDING)' 리스트가 조회되었습니다.",
                                                "detailMessage": null,
                                                "responseData": [
                                                    {
                                                        "userId": 1,
                                                        "nickname": "참여자1",
                                                        "adminId": null,
                                                        "email": "user1@example.com",
                                                        "status": "PENDING",
                                                        "joinedAt": "2024-11-17T12:15:00"
                                                    },
                                                    {
                                                        "userId": 2,
                                                        "nickname": "참여자2",
                                                        "adminId": null,
                                                        "email": "user2@example.com",
                                                        "status": "PENDING",
                                                        "joinedAt": "2024-11-17T12:20:00"
                                                    }
                                                ],
                                                "timestamp": "2024-11-17T13:00:00.123456"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/participants/pending-list/{runId}")
    public Response<List<GeneralParticipantsResponse>> getAllPendingParticipants(
            @Parameter(description = "일반 게시물 ID", required = true) @PathVariable Long runId);



}
