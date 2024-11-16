package com.github.accountmanagementproject.web.controller.runJoinPost.crewJoinPost;

import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
import com.github.accountmanagementproject.web.dto.responsebuilder.Response;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostCreateRequest;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Crew Run Join Post", description = "크루 달리기 참여 게시물 관련 API")
public interface CrewRunJoinPostControllerDocs {

    @Operation(summary = "게시글 생성", description = "새로운 크루 게시글을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "게시글 생성 성공",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "게시글 생성 성공 예시", value = """
                    {
                      "resultCode": "success",
                      "code": 201,
                      "httpStatus": "CREATED",
                      "message": "크루 게시물이 생성되었습니다.",
                      "responseData": {
                        "runId": 3,
                        "crewId": 24,
                        "authorId": 6,
                        "title": "주말 크루 달리기 모집3",
                        "content": "이번 주말에 함께 달리기 하실 분을 모집합니다!3",
                        "maximumPeople": 22,
                        "people": 0,
                        "location": "서울 강남구",
                        "status": "모집중",
                        "postType": "크루",
                        "inputLocation": "강남역",
                        "inputLatitude": 37.4979,
                        "inputLongitude": 127.0276,
                        "targetLocation": "선릉역",
                        "targetLatitude": 38.5045,
                        "targetLongitude": 127.0498,
                        "distance": 111.9457132752723,
                        "createdAt": "2024-11-07T05:35:48.194831",
                        "updatedAt": null,
                        "fileDtos": []
                      }
                    }""")
            )
    )
    Response<CrewRunPostResponse> createCrewPost(
            @Parameter(description = "크루 게시글 생성 요청 정보") CrewRunPostCreateRequest request,
            @Parameter(description = "크루 ID") Long crewId,
            @Parameter(hidden = true) @AuthenticationPrincipal String principal);

    @Operation(summary = "게시글 상세 조회", description = "주어진 runId에 해당하는 크루 게시글을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 조회 성공",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "게시글 상세 조회 성공 예시", value = """
                    {
                      "resultCode": "success",
                      "code": 200,
                      "httpStatus": "OK",
                      "message": "크루 게시물이 정상 조회되었습니다.",
                      "responseData": {
                        "runId": 1,
                        "crewId": 24,
                        "authorId": 6,
                        "title": "주말 크루 달리기 모집",
                        "content": "이번 주말에 함께 달리기 하실 분을 모집합니다!",
                        "maximumPeople": 20,
                        "people": 0,
                        "location": "서울 강남구",
                        "date": "2023-11-10",
                        "startTime": null,
                        "status": "모집중",
                        "postType": "크루",
                        "inputLocation": "강남역",
                        "inputLatitude": 37.4979,
                        "inputLongitude": 127.0276,
                        "targetLocation": "선릉역",
                        "targetLatitude": 37.5045,
                        "targetLongitude": 127.0498,
                        "distance": 2.091375914267994,
                        "createdAt": "2024-11-07T05:34:33",
                        "updatedAt": null,
                        "fileDtos": []
                      }
                    }""")
            )
    )
    Response<CrewRunPostResponse> getCrewPostByRunId(
            @Parameter(description = "게시글 ID") Long runId,
            @Parameter(hidden = true) @AuthenticationPrincipal String principal);

    @Operation(summary = "게시글 수정", description = "기존의 크루 게시글을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 수정 성공",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "게시글 수정 성공 예시", value = """
                    {
                      "resultCode": "success",
                      "code": 200,
                      "httpStatus": "OK",
                      "message": "크루 게시물이 정상 수정되었습니다.",
                      "responseData": {
                        "runId": 3,
                        "crewId": 24,
                        "authorId": 6,
                        "title": "주말 크루 달리기 모집3",
                        "content": "이번 주말에 함께 달리기 하실 분을 모집합니다!3",
                        "maximumPeople": 22,
                        "people": 0,
                        "location": "서울 강남구",
                        "date": "2023-11-10",
                        "startTime": null,
                        "status": "모집중",
                        "postType": "크루",
                        "inputLocation": "강남역",
                        "inputLatitude": 37.4979,
                        "inputLongitude": 127.0276,
                        "targetLocation": "선릉역",
                        "targetLatitude": 38.5045,
                        "targetLongitude": 127.0498,
                        "distance": 111.9457132752723,
                        "createdAt": "2024-11-07T05:35:48",
                        "updatedAt": null,
                        "fileDtos": []
                      }
                    }""")
            )
    )
    Response<CrewRunPostResponse> updateCrewPost(
            @Parameter(description = "크루 ID") Long crewId,
            @Parameter(description = "게시글 ID") Long runId,
            @Parameter(description = "수정할 게시글 정보") CrewRunPostUpdateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal String principal);

    @Operation(summary = "게시글 삭제", description = "기존의 크루 게시글을 삭제합니다.")
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
    Response<Void> deleteCrewPost(
            @Parameter(description = "크루 ID") Long crewId,
            @Parameter(description = "게시글 ID") Long runId,
            @Parameter(hidden = true) @AuthenticationPrincipal String principal);


    @Operation(summary = "크루 달리기 모집 게시물 목록 조회", description = "해당 크루에 속한 모든 모집 게시물을 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "모집 게시물 조회 성공",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "모집 게시물 목록 조회 예시", value = """
                    {
                      "resultCode": "success",
                      "code": 200,
                      "httpStatus": "OK",
                      "message": "모든 게시물이 조회되었습니다.",
                      "responseData": {
                        "content": [
                          {
                            "runId": 11,
                            "crewId": 24,
                            "authorId": 6,
                            "title": "주말 크루 달리기 모집3",
                            "content": "이번 주말에 함께 달리기 하실 분을 모집합니다!3",
                            "maximumPeople": 22,
                            "people": 0,
                            "location": "서울 강남구",
                            "date": "2023-11-10",
                            "startTime": null,
                            "status": "모집중",
                            "postType": "크루",
                            "inputLocation": "강남역",
                            "inputLatitude": 37.4979,
                            "inputLongitude": 127.0276,
                            "targetLocation": "선릉역",
                            "targetLatitude": 38.5045,
                            "targetLongitude": 127.0498,
                            "distance": 111.9457132752723,
                            "createdAt": "2024-11-07T07:47:47",
                            "updatedAt": null,
                            "fileDtos": []
                          }
                        ],
                        "currentPage": 0,
                        "size": 9,
                        "hasMore": true
                      }
                    }""")
            )
    )
    @Parameters({
            @Parameter(name = "cursor", description = "커서 기반 페이징을 위한 마지막 항목의 ID. 첫 요청 시에는 null로 전달", example = "10"),
            @Parameter(name = "size", description = "페이지 당 항목 수. 기본값은 20", example = "9"),
            @Parameter(name = "location", description = "필터링할 지역. 기본값은 '전체'", example = "전체"),
            @Parameter(name = "date", description = "조회 기준 날짜. yyyy-MM-dd 형식", example = "2023-11-10"),
            @Parameter(name = "sortType", description = "정렬 방식: 'newest' (최신순) 또는 'oldest' (오래된 순)", example = "newest")
    })
    Response<PageResponseDto<CrewRunPostResponse>> getAll(
            PageRequestDto pageRequestDto, @AuthenticationPrincipal String principal);

}

