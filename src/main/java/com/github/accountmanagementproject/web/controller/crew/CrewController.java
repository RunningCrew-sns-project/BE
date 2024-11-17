package com.github.accountmanagementproject.web.controller.crew;

import com.github.accountmanagementproject.service.crew.CrewService;
import com.github.accountmanagementproject.web.dto.crew.CrewCreationRequest;
import com.github.accountmanagementproject.web.dto.crew.CrewDetailWithPostsResponse;
import com.github.accountmanagementproject.web.dto.infinitescrolling.criteria.SearchRequest;
import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/crews")
public class CrewController implements CrewControllerDocs {
    private final CrewService crewService;



    @GetMapping
    public CustomSuccessResponse getAvailableCrewLists(
            @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Long cursorId ,@RequestParam(required = false) boolean reverse,
            @RequestParam(required = false) String criteria, @AuthenticationPrincipal String email,
            @RequestParam(required = false) String crewRegion) {
        return new CustomSuccessResponse.SuccessDetail()
                .message("크루 목록 조회 성공")
                .responseData(crewService.getAvailableCrewLists(email, new SearchRequest(size, reverse, criteria, cursor, cursorId, crewRegion)))
                .build();
    }


    @Override
    @PostMapping
    public ResponseEntity<CustomSuccessResponse> createCrew(@RequestBody @Valid CrewCreationRequest request,
                                                            @AuthenticationPrincipal String email) {
        crewService.crewCreation(request, email);
        CustomSuccessResponse response = new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.CREATED).message("크루 생성 성공").build();
        return new ResponseEntity<>(response, response.getSuccess().getHttpStatus());
    }

    @PostMapping("/{crewId}/join")
    public CustomSuccessResponse joinCrew(@AuthenticationPrincipal String email,
                                          @PathVariable Long crewId) {
        return new CustomSuccessResponse.SuccessDetail()
                .message("크루 가입 신청 성공")
                .responseData(crewService.joinTheCrew(email, crewId))
                .build();
    }

    @GetMapping("/request-test")
    public CustomSuccessResponse requestTest(@AuthenticationPrincipal String email) {

        return new CustomSuccessResponse.SuccessDetail()
                .message("요청 테스트 성공")
                .responseData(crewService.requestTest(email))
                .build();
    }


    @GetMapping("/{crewId}")
    public CustomSuccessResponse getCrewDetail(@PathVariable Long crewId) {
        return new CustomSuccessResponse.SuccessDetail()
                .message("크루 상세 조회 성공")
                .responseData(crewService.getCrewDetail(crewId))
                .build();
    }

    @GetMapping("/{crewId}/about-user")
    public CustomSuccessResponse userAboutCrew(@PathVariable Long crewId, @AuthenticationPrincipal String email) {
        return new CustomSuccessResponse.SuccessDetail()
                .message("크루에 대한 유저 정보 조회 성공")
                .responseData(crewService.userAboutCrew(email, crewId))
                .build();
    }

    @GetMapping("/{crewId}/admin/users")
    public CustomSuccessResponse getCrewUsers(@PathVariable Long crewId, @RequestParam(required = false) Boolean all, @AuthenticationPrincipal String masterEmail) {
        return new CustomSuccessResponse.SuccessDetail()
                .message("크루 멤버 조회 성공")
                .responseData(crewService.getCrewUsers(masterEmail, crewId, all))
                .build();
    }
    @GetMapping("/{crewId}/users")
    public CustomSuccessResponse getSimplyCrewUsers(@PathVariable Long crewId) {
        return new CustomSuccessResponse.SuccessDetail()
                .message("크루 멤버 조회 성공")
                .responseData(crewService.getSimplyCrewUsers(crewId))
                .build();
    }

    @DeleteMapping("/{crewId}/users")
    public CustomSuccessResponse withdrawalCrew(@PathVariable Long crewId, @AuthenticationPrincipal String email) {
        crewService.withdrawalCrew(email, crewId);
        return new CustomSuccessResponse.SuccessDetail()
                .message("크루 탈퇴 성공")
                .build();
    }

    //퇴장 시키기
    @Override
    @DeleteMapping("/sendOutCrew")
    public CustomSuccessResponse sendOutCrew(@AuthenticationPrincipal String email,
                                             @RequestParam Long crewId,
                                             @RequestParam Long outCrewsUserId) {
        return new CustomSuccessResponse.SuccessDetail()
                .message("퇴장시키기 성공")// 엔티티에 유저아이디가 Long타입이라 long타입으로 수정햇습니당
                .responseData(crewService.sendOutCrew(email, crewId, outCrewsUserId))
                .build();
    }

    //승인, 거절
    @Override
    @PostMapping("/approveOrReject")
    public CustomSuccessResponse approveOrReject(@AuthenticationPrincipal String email,
                                                 @RequestParam Long crewId,
                                                 @RequestParam Long requestCrewUserId,
                                                 @RequestParam Boolean approveOrReject) {
        return new CustomSuccessResponse.SuccessDetail()
                .message("처리가 완료되었습니다.")
                .responseData(crewService.approveOrReject(email, crewId, requestCrewUserId, approveOrReject))
                .build();
    }


    // 크루 Info + 크루 달리기 게시물 목록
    @CrossOrigin(
            origins = {"http://localhost:8080", "http://54.180.9.220:8080"},
            allowedHeaders = "*",
            allowCredentials = "true"
    )
    @GetMapping("/{crewId}/list")
//    @Override
    public CustomSuccessResponse getCrewDetailsWithCrewPosts(
            @PathVariable Long crewId, @RequestParam String email, PageRequestDto pageRequestDto) {

        PageResponseDto<CrewDetailWithPostsResponse> response = crewService.getCrewDetailsWithPosts(email, crewId, pageRequestDto);

        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.OK)
                .message("크루 정보와 리스트가 정상적으로 조회되었습니다.")
                .responseData(response)
                .build();
    }


    @PutMapping("/{crewId}/admin/users")
    public CustomSuccessResponse userAYellowCard(@PathVariable Long crewId, @RequestParam Long badUserId, @AuthenticationPrincipal String masterEmail) {
        return new CustomSuccessResponse.SuccessDetail()
                .message("경고 성공")
                .responseData(crewService.giveAUserAYellowCard(masterEmail, crewId, badUserId))
                .build();
    }

}