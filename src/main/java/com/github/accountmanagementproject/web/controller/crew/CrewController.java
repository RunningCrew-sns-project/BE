package com.github.accountmanagementproject.web.controller.crew;

import com.github.accountmanagementproject.service.crew.CrewService;
import com.github.accountmanagementproject.web.dto.crew.CrewCreationRequest;
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
public class CrewController implements CrewControllerDocs{
    private final CrewService crewService;

    @Override
    @PostMapping
    public ResponseEntity<CustomSuccessResponse> createCrew(@RequestBody @Valid CrewCreationRequest request,
                                                            @AuthenticationPrincipal String email){
        crewService.crewCreation(request, email);
        CustomSuccessResponse response = new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.CREATED).message("크루 생성 성공").build();
        return new ResponseEntity<>(response, response.getSuccess().getHttpStatus());
    }

    @PostMapping("/{crewId}/join")
    public CustomSuccessResponse joinCrew(@AuthenticationPrincipal String email,
                                            @PathVariable Long crewId){
        return new CustomSuccessResponse.SuccessDetail()
                .message("크루 가입 성공")
                .responseData(crewService.joinTheCrew(email, crewId))
                .build();
    }
    @GetMapping("/request-test")
    public CustomSuccessResponse requestTest(@AuthenticationPrincipal String email){

        return new CustomSuccessResponse.SuccessDetail()
                .message("요청 테스트 성공")
                .responseData(crewService.requestTest(email))
                .build();
    }



    @GetMapping("/{crewId}")
    public CustomSuccessResponse getCrewDetail(@PathVariable Long crewId){
        return new CustomSuccessResponse.SuccessDetail()
                .message("크루 상세 조회 성공")
                .responseData(crewService.getCrewDetail(crewId))
                .build();
    }
    @GetMapping("/{crewId}/users")
    public CustomSuccessResponse getCrewUsers(@PathVariable Long crewId, @RequestParam(required = false) Boolean all, @AuthenticationPrincipal String masterEmail){
        return new CustomSuccessResponse.SuccessDetail()
                .message("크루 멤버 조회 성공")
                .responseData(crewService.getCrewUsers(masterEmail, crewId, all))
                .build();
    }

    //퇴장 시키기
    @DeleteMapping("/sendOutCrew")
    public CustomSuccessResponse sendOutCrew(@AuthenticationPrincipal String email,
                                             @RequestParam Long crewId,
                                             @RequestParam Long outUserId){
        return new CustomSuccessResponse.SuccessDetail()
                .message("퇴장시키기 성공")// 엔티티에 유저아이디가 Long타입이라 long타입으로 수정햇습니당
                .responseData(crewService.sendOutCrew(email, crewId, outUserId))
                .build();
    }

    //승인, 거절
    @PostMapping("/approveOrReject")
    public CustomSuccessResponse approveOrReject(@AuthenticationPrincipal String email,
                                                 @RequestParam Long crewId,
                                                 @RequestParam Integer requestCrewUserId,
                                                 @RequestParam Boolean approveOrReject){
        return new CustomSuccessResponse.SuccessDetail()
                .message("처리가 완료되었습니다.")
                .responseData(crewService.approveOrReject(email, crewId, requestCrewUserId, approveOrReject))
                .build();
    }
}
