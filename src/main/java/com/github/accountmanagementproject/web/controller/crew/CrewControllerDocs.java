package com.github.accountmanagementproject.web.controller.crew;

import com.github.accountmanagementproject.web.dto.crew.CrewCreationRequest;
import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Crew", description = "Crew 관련 API")
public interface CrewControllerDocs {
    @Operation(summary = "크루원 간단 조회", description = "크루원 조회")
    CustomSuccessResponse getSimplyCrewUsers(Long crewId);
    @Operation(summary = "특정 크루에 대한 나의 정보 조회", description = "크루에 대한 나의 정보 조회")
    CustomSuccessResponse userAboutCrew(Long crewId, String email);

    @Operation(summary = "크루 목록 조회", description = "크루 목록을 조회합니다.<br>" +
            "criteria = 정렬 조건 (latest = 최신순, popular = 인기순, name = 이름순, member = 크루원수순, activities = 활동량(게시글 수))<br>" +
            "인기순은 한달간 가입한 크루원 수로 판단하고, 활동량은 크루원들의 일주일간 게시글 수로 판단합니다.")
    CustomSuccessResponse getAvailableCrewLists(
            @Parameter(description = "한번 요청에 보여줄 크루 수 기본값 = 20", example = "20")
            int size,
            @Parameter(description = "다음 페이지를 요청할 때 필요한 커서값 (이전 응답의 nextCursor 의 값 그대로 요청)", example = "1")
            String cursor,
            @Parameter(description = "다음 페이지를 요청할 때 필요한 커서Id값 (이전 응답의 nextCursorId 의 값 그대로 요청)", example = "1")
            Long cursorId,
            @Parameter(description = "값을 역순으로 받을지 여부", example = "false")
            boolean reverse,
            @Parameter(description = "정렬 조건", schema = @Schema(allowableValues = {"latest", "popular", "name", "member", "activities"}))
            String criteria,
            String email,
            @Parameter(description = "크루 활동 지역", schema = @Schema(allowableValues = {"서울", "경기도", "인천", "부산", "대구", "광주", "대전", "울산", "강원도", "충청도", "전라도", "경상도", "제주도"}))
            String crewRegion);


    @Operation(summary = "크루 생성", description = "크루를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "크루 생성 성공",
            content = @Content(mediaType = "application/json",
                    examples =
                    @ExampleObject(name = "크루 생성 성공 응답",
                            description = "⬆️⬆️ 상태코드 201로 내려주며 리스폰 데이터는 따로 없습니다.",
                            value = """
                                    {
                                      "success": {
                                        "code": 201,
                                        "httpStatus": "CREATED",
                                        "message": "크루 생성 성공",
                                        "timestamp": "2024-10-16T15:31:55.6752972"
                                      }
                                    }""")
            )
    )
    @ApiResponse(responseCode = "400", description = "필수 값 누락",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "크루 생성에 필요한 필수 값 누락",
                                    description = "⬆️⬆️ 크루 활동 지역에 빈스트링(\"\")이 들어올때",
                                    value = """
                                            {
                                               "error": {
                                                 "code": 400,
                                                 "httpStatus": "BAD_REQUEST",
                                                 "systemMessage": "유효성 검사 실패",
                                                 "customMessage": "크루 활동 지역은 필수입니다.",
                                                 "request": "activityRegion : ",
                                                 "timestamp": "2024-10-29 19:03:23"
                                               }
                                             }"""),
                            @ExampleObject(name = "크루 이름 누락",
                                    description = "⬆️⬆️ 크루 이름에 null 값이 들어올때",
                                    value = """
                                            {
                                              "error": {
                                                "code": 400,
                                                "httpStatus": "BAD_REQUEST",
                                                "systemMessage": "유효성 검사 실패",
                                                "customMessage": "크루 이름은 필수입니다.",
                                                "request": "crewName : null",
                                                "timestamp": "2024-10-29 19:04:45"
                                              }
                                            }""")
                    })
    )
    ResponseEntity<CustomSuccessResponse> createCrew(CrewCreationRequest request,
                                                     String email);

    @Operation(summary = "크루 가입", description = "크루에 가입하는 API")
    @ApiResponse(responseCode = "201", description = "크루 생성 성공",
            content = @Content(mediaType = "application/json",
                    examples =
                    @ExampleObject(name = "크루 생성 성공 응답",
                            description = "⬆️⬆️ 상태코드 200, responseData에 가입 정보를 담아 응답합니다.",
                            value = """
                                    {
                                      "success": {
                                        "code": 200,
                                        "httpStatus": "OK",
                                        "message": "크루 가입 성공",
                                        "responseData": {
                                          "crewName": "런닝크루1",
                                          "status": "가입 대기",
                                          "applicationDate": "2024-10-29"
                                        },
                                        "timestamp": "2024-10-29T23:21:47.4293975"
                                      }
                                    }""")
            )
    )
    @ApiResponse(responseCode = "409", description = "값 중복",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "이미 가입 중인 크루 또는 가입된 크루에 가입 요청시",
                                    description = "⬆️⬆️ 이미 가입 중인 크루 또는 가입된 크루에 가입 요청시",
                                    value = """
                                            {
                                              "error": {
                                                "code": 409,
                                                "httpStatus": "CONFLICT",
                                                "systemMessage": "유효성 검사 실패",
                                                "customMessage": "이미 가입했거나 가입 요청 중인 크루입니다.",
                                                "request": "런닝크루1",
                                                "timestamp": "2024-10-29 23:23:00"
                                              }
                                            }"""),
                            @ExampleObject(name = "자신이 크루마스터인 크루에 가입요청시",
                                    description = "⬆️⬆️ 자신이 크루마스터인 크루에 가입요청시",
                                    value = """
                                            {
                                               "error": {
                                                 "code": 409,
                                                 "httpStatus": "CONFLICT",
                                                 "systemMessage": "DuplicateKeyException",
                                                 "customMessage": "자기가 만든 크루에 가입할 수 없습니다.",
                                                 "request": "런닝크루9",
                                                 "timestamp": "2024-10-29 23:24:12"
                                               }
                                             }""")
                    })
    )
    CustomSuccessResponse joinCrew(String email, Long crewId);

    @Operation(summary = "나의 크루 가입 요청내역보기", description = "테스트를 위한 가입요청내역 반환")
    CustomSuccessResponse requestTest(String email);

    @Operation(summary = "크루 상세 조회", description = "크루 상세 정보를 조회합니다.<br>" +
            "crewMaster = 크루마스터닉네임, activityRegion = 크루 활동지역, createdAt = 크루 생성일, memberCount = 크루원 수, maxCapacity = 크루 최대인원수")
    @ApiResponse(responseCode = "200", description = "크루 상세 조회 성공",
            content = @Content(mediaType = "application/json",
                    examples =
                    @ExampleObject(name = "크루 상세 조회 응답",
                            description = "⬆️⬆️ 상태코드 200, responseData에 상세 정보를 담아 응답합니다.",
                            value = """
                                    {
                                       "success": {
                                         "code": 200,
                                         "httpStatus": "OK",
                                         "message": "크루 상세 조회 성공",
                                         "responseData": {
                                           "crewName": "런닝크루6",
                                           "crewIntroduction": "크루설명입니당",
                                           "crewImageUrl": "https://png.pngtree.com/png-clipart/20200625/ourmid/pngtree-boys-sports-running-fitness-png-image_2266304.jpg",
                                           "crewMaster": "졸령",
                                           "activityRegion": "송파",
                                           "createdAt": "2024-10-28T12:46:38",
                                           "memberCount": 1,
                                           "maxCapacity": 70
                                         },
                                         "timestamp": "2024-10-31T21:21:57.8307578"
                                       }
                                     }""")
            )
    )
    @ApiResponse(responseCode = "404", description = "크루 없음",
            content = @Content(mediaType = "application/json",
                    examples =
                    @ExampleObject(name = "크루 찾기 실패 응답",
                            description = "⬆️⬆️ 상태코드 404 해당 crewId로 크루를 찾을 수 없을때",
                            value = """
                                    {
                                       "error": {
                                         "code": 404,
                                         "httpStatus": "NOT_FOUND",
                                         "customMessage": "해당 크루를 찾을 수 없습니다.",
                                         "request": 1,
                                         "timestamp": "2024-10-31 21:25:20"
                                       }
                                     }""")
            )
    )
    CustomSuccessResponse getCrewDetail(@Parameter(description = "크루 고유번호", example = "5") Long crewId);

    @Operation(summary = "크루원 조회(크루 관리자페이지 기능)", description = "크루마스터가 크루에 가입한 유저들을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "크루 유저 조회 성공",
            content = @Content(mediaType = "application/json",
                    examples =
                    @ExampleObject(name = "크루원 조회 응답",
                            description = "⬆️⬆️ 상태코드 200, responseData에 유저정보들을 담아 응답합니다.",
                            value = """
                                    {
                                        "success": {
                                          "code": 200,
                                          "httpStatus": "OK",
                                          "message": "크루 멤버 조회 성공",
                                          "responseData": [
                                            {
                                              "email": "abc11@abc.com",
                                              "nickname": "이브라히모비치",
                                              "userImageUrl": "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/anonymous-user-icon.png",
                                              "profileMessage": "달리고 싶다..⭐",
                                              "gender": "미정",
                                              "lastLoginDate": null,
                                              "status": "가입 완료",
                                              "caveat": 0,
                                              "joinRequestOrJoinDate": "2024-11-05T14:43:17"
                                            },
                                            {
                                              "email": "sihu1205@gmail.com",
                                              "nickname": "이시후",
                                              "userImageUrl": "http://k.net/dn/lvFN7/btsIhygOK2H/ZrHKAPfi20EL3oN520F65K/img_640x640.jpg",
                                              "profileMessage": null,
                                              "gender": "미정",
                                              "lastLoginDate": null,
                                              "status": "가입 대기",
                                              "caveat": 0,
                                              "joinRequestOrJoinDate": "2024-11-05T05:42:42"
                                            },
                                            {
                                              "email": "sihu93@gmail.com",
                                              "nickname": "이시후",
                                              "userImageUrl": "http://k.net/dn/4viK0/btsGJlJED2t/vgKsKRzmXGMQwHk75gvY70/img_640x640.jpg",
                                              "profileMessage": null,
                                              "gender": "남성",
                                              "lastLoginDate": null,
                                              "status": "가입 대기",
                                              "caveat": 0,
                                              "joinRequestOrJoinDate": "2024-11-05T05:42:32"
                                            },
                                            {
                                              "email": "ahhyun2008@naver.com",
                                              "nickname": "조현아",
                                              "userImageUrl": "http://img1.kakaocdn.net/thumb/R640x640.q70/?fname=http://t1.kakaocdn.net/account_images/default_profile.jpeg",
                                              "profileMessage": "상태메세지",
                                              "gender": "여성",
                                              "lastLoginDate": "2024-11-04T14:12:28",
                                              "status": "가입 완료",
                                              "caveat": 0,
                                              "joinRequestOrJoinDate": "2024-11-05T14:43:19"
                                            },
                                            {
                                              "email": "abc2@abc.com",
                                              "nickname": "닉네임수정테스트",
                                              "userImageUrl": "https://uxwing.com/wp-content/themes/uxwing/download/sihu-avatars/woman-user-color-icon.png",
                                              "profileMessage": null,
                                              "gender": "여성",
                                              "lastLoginDate": "2024-11-05T14:28:10",
                                              "status": "가입 대기",
                                              "caveat": 0,
                                              "joinRequestOrJoinDate": "2024-10-29T23:23:58"
                                            }
                                          ],
                                          "timestamp": "2024-11-05T14:44:19.8903104"
                                        }
                                      }""")
            )
    )
    @ApiResponse(responseCode = "401", description = "크루 마스터아님",
            content = @Content(mediaType = "application/json",
                    examples =
                    @ExampleObject(name = "권한 에러",
                            description = "⬆️⬆️ 상태코드 401 해당 crewId의 크루 마스터가 아닐때",
                            value = """
                                    {
                                        "error": {
                                          "code": 401,
                                          "httpStatus": "UNAUTHORIZED",
                                          "customMessage": "크루 마스터가 아닙니다",
                                          "request": "abc2@abc.com",
                                          "timestamp": "2024-11-05 14:46:39"
                                        }
                                      }""")
            )
    )
    CustomSuccessResponse getCrewUsers(Long crewId, @Parameter(description = "null = 가입완료 크루원, true = 전체, false = 요청상태 크루원") Boolean all,String masterEmail);


    //퇴장 시키기
//    @Parameter(name = "crewId", description = "크루 아이디")
//    @Parameter(name = "outCrewsUsersId", description = "내보낼 유저의 아이디")
    @Operation(summary = "크루원 강퇴 시키기", description = "crewId와 내보낼 크루원의 userid를 받아 강퇴 기능 구현")
    @DeleteMapping("/sendOutCrew")
    CustomSuccessResponse sendOutCrew(@AuthenticationPrincipal String email,
                                      @RequestParam(name = "크루 아이디") Long crewId,
                                      @RequestParam(name = "내보낼 유저 아이디") Long outCrewsUserId);

    //승인, 거절
    @Parameter(name = "crewId", description = "크루 아이디")
    @Parameter(name = "requestCrewUserId", description = "가입신청한 유저 아이디")
    @Parameter(name = "approveOrReject", description = "승인/거절 값", examples = {@ExampleObject(name = "승인", value = "true"),
            @ExampleObject(name = "거절", value = "false")}
    )

    @Operation(summary = "크루 가입 신청, 거절", description = "crewId와 요청 유저 아이디, 승인/거절 요청 값을 받아 승인/거절 로직 구현")
    @PostMapping("/approveOrReject")
    CustomSuccessResponse approveOrReject(@AuthenticationPrincipal String email,
                                          @RequestParam(name = "크루 아이디") Long crewId,
                                          @RequestParam(name = "요청 유저 아이디") Long requestCrewUserId,
                                          @RequestParam(name = "승인/거절") Boolean approveOrReject);


    @Operation(
            summary = "크루 상세 정보와 달리기 게시물 목록 조회",
            description = "특정 크루의 상세 정보와 크루에 연관된 달리기 참여 게시물 목록을 페이징하여 반환합니다."
    )
    @Parameters({
            @Parameter(name = "email", description = "유저 이메일", example = "", required = true),
            @Parameter(name = "crewId", description = "크루의 고유 ID", example = "42", required = true),
            @Parameter(name = "cursor", description = "커서 기반 페이징을 위한 마지막 항목의 ID. 첫 요청 시에는 null로 전달", example = "10"),
            @Parameter(name = "size", description = "페이지 당 항목 수. 기본값은 10", example = "10"),
            @Parameter(name = "location", description = "필터링할 지역. 기본값은 '전체'", example = "전체"),
            @Parameter(name = "date", description = "조회 기준 날짜. yyyy-MM-dd 형식", example = "2023-11-10"),
            @Parameter(name = "sortType", description = "정렬 방식: 'newest' (최신순) 또는 'oldest' (오래된 순)", example = "newest")
    })
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "크루 상세 정보 및 게시물 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageResponseDto.class),
                            examples = @ExampleObject(value = """
                {
                  "content": {
                    "crewInfo": {
                      "crewId": 42,
                      "crewName": "런닝크루",
                      "crewIntroduction": "즐거운 달리기를 함께해요!",
                      "crewImageUrls": ["url1", "url2"],
                      "crewMaster": "Master Nickname",
                      "activityRegion": "서울",
                      "createdAt": "2023-11-10T12:00:00",
                      "memberCount": 20,
                      "maxCapacity": 100
                    },
                    "items": [
                      {
                        "runId": 101,
                        "crewId": 42,
                        "authorId": 1001,
                        "crewName": "런닝크루",
                        "crewDescription": "즐거운 달리기 크루입니다.",
                        "crewImageUrl": "url1",
                        "title": "한강 달리기",
                        "content": "한강에서 달리기를 즐겨요!",
                        "maximumPeople": 15,
                        "people": 10,
                        "location": "서울",
                        "date": "2023-11-12",
                        "startTime": "07:00:00",
                        "status": "ACTIVE",
                        "postType": "PUBLIC",
                        "inputLocation": "한강공원",
                        "inputLatitude": 37.5326,
                        "inputLongitude": 127.0246,
                        "targetLocation": "뚝섬",
                        "targetLatitude": 37.541,
                        "targetLongitude": 127.056,
                        "distance": 5.0,
                        "createdAt": "2023-11-10T12:00:00",
                        "updatedAt": "2023-11-10T12:00:00",
                        "banners": [
                          {"fileName": "banner1.png", "imageUrl": "banner_url1"},
                          {"fileName": "banner2.png", "imageUrl": "banner_url2"}
                        ]
                      }
                    ]
                  },
                  "countPerScroll": 10,
                  "lastScroll": false,
                  "nextCursor": 102
                }
            """)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "크루를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 요청을 처리할 수 없습니다.")
    })
    CustomSuccessResponse getCrewDetailsWithCrewPosts(@PathVariable Long crewId, @AuthenticationPrincipal String email, PageRequestDto pageRequestDto);
}