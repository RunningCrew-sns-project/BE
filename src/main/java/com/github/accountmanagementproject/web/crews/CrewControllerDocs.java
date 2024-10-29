package com.github.accountmanagementproject.web.crews;

import com.github.accountmanagementproject.web.dto.crews.CrewCreationRequest;
import com.github.accountmanagementproject.web.dto.responseSystem.CustomSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Crew", description = "Crew 관련 API")
public interface CrewControllerDocs {

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
}
