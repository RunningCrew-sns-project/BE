package com.github.accountmanagementproject.web.controller.account;

import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MyPageCrewPost", description = "마이페이지 크루 및 게시글 관련 API")
public interface MyPageCrewAndPostControllerDocs {

    @Operation(summary = "내 크루 조회", description = "내가 만든 크루와 가입한 크루를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json",
                    examples =
                    @ExampleObject(name = "가입완료상태 크루만 조회 했을시",
                            description = "전체조회와 가입완료 크루만 조회했을때는 내가 만든 크루도 조회됩니다.",
                            value = """
                                    {
                                      "success": {
                                        "code": 200,
                                        "httpStatus": "OK",
                                        "message": "내 크루 조회 성공",
                                        "responseData": [
                                          {
                                            "crewId": 3,
                                            "crewName": "런닝크루",
                                            "crewImageUrl": "https://dimg.donga.com/wps/NEWS/IMAGE/2024/10/08/130174015.1.jpg",
                                            "crewIntroduction": "크루설명입니당",
                                            "requestOrCompletionDate": "2024-07-04T02:53:36",
                                            "crewMaster": true
                                          },
                                          {
                                            "crewId": 4,
                                            "crewName": "크루이름",
                                            "crewImageUrl": "https://blog.kakaocdn.net/dn/chEHhC/btsHfDpWlgN/NmykK6dzj9XlA3tb9oXoR1/img.webp",
                                            "crewIntroduction": "크루 크루크루",
                                            "requestOrCompletionDate": "2024-06-04T02:53:38",
                                            "crewMaster": true
                                          },
                                          {
                                            "crewId": 5,
                                            "crewName": "런닝크루1",
                                            "crewImageUrl": "https://img.hani.co.kr/imgdb/resize/2023/0609/168620259122_20230609.JPG",
                                            "crewIntroduction": "크루설명입니당",
                                            "requestOrCompletionDate": "2024-11-20T02:53:39",
                                            "crewMaster": true
                                          },
                                          {
                                            "crewId": 6,
                                            "crewName": "런닝크루2",
                                            "crewImageUrl": "https://www.ikbn.news/data/photos/20240415/art_17125497941069_47ebf1.png",
                                            "crewIntroduction": "크루설명입니당",
                                            "requestOrCompletionDate": "2024-12-10T02:53:41",
                                            "crewMaster": true
                                          },
                                          {
                                            "crewId": 7,
                                            "crewName": "런닝크루3",
                                            "crewImageUrl": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTpYFrhsC-mQse9Qw3LOResbiBcWrejitts9ZfZkzi8i_E_tTGbQ3Q7W9fJyKiWzMxea7I&usqp=CAU",
                                            "crewIntroduction": "크루설명입니당",
                                            "requestOrCompletionDate": "2024-06-01T02:53:42",
                                            "crewMaster": true
                                          },
                                          {
                                            "crewId": 8,
                                            "crewName": "런닝크루4",
                                            "crewImageUrl": "https://png.pngtree.com/png-clipart/20200625/ourmid/pngtree-boys-sports-running-fitness-png-image_2266304.jpg",
                                            "crewIntroduction": "크루설명입니당",
                                            "requestOrCompletionDate": "2024-11-30T02:53:43",
                                            "crewMaster": true
                                          },
                                          {
                                            "crewId": 9,
                                            "crewName": "런닝크루5",
                                            "crewImageUrl": "https://dimg.donga.com/wps/NEWS/IMAGE/2024/10/08/130174015.1.jpg",
                                            "crewIntroduction": "크루설명입니당",
                                            "requestOrCompletionDate": "2024-10-29T23:23:58",
                                            "crewMaster": true
                                          },
                                          {
                                            "crewId": 11,
                                            "crewName": "런닝크루7",
                                            "crewImageUrl": "https://www.jeonmae.co.kr/news/photo/202307/967673_659431_312.jpg",
                                            "crewIntroduction": "크루설명입니당",
                                            "requestOrCompletionDate": "2024-10-29T23:23:58",
                                            "crewMaster": true
                                          },
                                          {
                                            "crewId": 15,
                                            "crewName": "런닝크루11",
                                            "crewImageUrl": "https://www.mstoday.co.kr/news/photo/202312/87148_77487_1720.jpg",
                                            "crewIntroduction": "크루설명입니당",
                                            "requestOrCompletionDate": "2024-10-29T23:23:58",
                                            "crewMaster": true
                                          },
                                          {
                                            "crewId": 16,
                                            "crewName": "런닝크루12",
                                            "crewImageUrl": "https://flexible.img.hani.co.kr/flexible/normal/832/826/imgdb/original/2024/1009/20241009502214.jpg",
                                            "crewIntroduction": "크루설명입니당",
                                            "requestOrCompletionDate": "2024-10-29T23:23:58",
                                            "crewMaster": true
                                          },
                                          {
                                            "crewId": 17,
                                            "crewName": "런닝크루13",
                                            "crewImageUrl": "https://www.mstoday.co.kr/news/photo/202312/87148_77487_1720.jpg",
                                            "crewIntroduction": "크루설명입니당",
                                            "requestOrCompletionDate": "2024-10-29T23:23:58",
                                            "crewMaster": true
                                          },
                                          {
                                            "crewId": 18,
                                            "crewName": "런닝크루14",
                                            "crewImageUrl": null,
                                            "crewIntroduction": "크루설명입니당",
                                            "requestOrCompletionDate": "2024-10-29T23:23:58",
                                            "crewMaster": true
                                          },
                                          {
                                            "crewId": 3,
                                            "crewName": "런닝크루",
                                            "crewImageUrl": "https://dimg.donga.com/wps/NEWS/IMAGE/2024/10/08/130174015.1.jpg",
                                            "crewIntroduction": "크루설명입니당",
                                            "requestOrCompletionDate": "2024-07-04T02:53:36",
                                            "crewMaster": false
                                          },
                                          {
                                            "crewId": 4,
                                            "crewName": "크루이름",
                                            "crewImageUrl": "https://blog.kakaocdn.net/dn/chEHhC/btsHfDpWlgN/NmykK6dzj9XlA3tb9oXoR1/img.webp",
                                            "crewIntroduction": "크루 크루크루",
                                            "requestOrCompletionDate": "2024-06-04T02:53:38",
                                            "crewMaster": false
                                          },
                                          {
                                            "crewId": 5,
                                            "crewName": "런닝크루1",
                                            "crewImageUrl": "https://img.hani.co.kr/imgdb/resize/2023/0609/168620259122_20230609.JPG",
                                            "crewIntroduction": "크루설명입니당",
                                            "requestOrCompletionDate": "2024-11-20T02:53:39",
                                            "crewMaster": false
                                          },
                                          {
                                            "crewId": 6,
                                            "crewName": "런닝크루2",
                                            "crewImageUrl": "https://www.ikbn.news/data/photos/20240415/art_17125497941069_47ebf1.png",
                                            "crewIntroduction": "크루설명입니당",
                                            "requestOrCompletionDate": "2024-12-10T02:53:41",
                                            "crewMaster": false
                                          },
                                          {
                                            "crewId": 7,
                                            "crewName": "런닝크루3",
                                            "crewImageUrl": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTpYFrhsC-mQse9Qw3LOResbiBcWrejitts9ZfZkzi8i_E_tTGbQ3Q7W9fJyKiWzMxea7I&usqp=CAU",
                                            "crewIntroduction": "크루설명입니당",
                                            "requestOrCompletionDate": "2024-06-01T02:53:42",
                                            "crewMaster": false
                                          },
                                          {
                                            "crewId": 8,
                                            "crewName": "런닝크루4",
                                            "crewImageUrl": "https://png.pngtree.com/png-clipart/20200625/ourmid/pngtree-boys-sports-running-fitness-png-image_2266304.jpg",
                                            "crewIntroduction": "크루설명입니당",
                                            "requestOrCompletionDate": "2024-11-30T02:53:43",
                                            "crewMaster": false
                                          }
                                        ],
                                        "timestamp": "2024-11-04T03:07:03.7696817"
                                      }
                                    }""")
            )
    )
    CustomSuccessResponse getMyCrew(String email, @Parameter(name = "all", description = "null = 내가 만든 크루와 가입완료 크루, true = 전체, false = 요청중인 크루") Boolean all);
}