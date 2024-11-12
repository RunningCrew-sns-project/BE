package com.github.accountmanagementproject.web.controller.runJoinPost.generalJoinPost;

import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
import com.github.accountmanagementproject.web.dto.responsebuilder.Response;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostCreateRequest;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


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
            @Parameter(description = "유저 이메일") String email);

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
            @Parameter(description = "유저 이메일") String email);

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
            @Parameter(description = "유저 이메일") String email);

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
}
