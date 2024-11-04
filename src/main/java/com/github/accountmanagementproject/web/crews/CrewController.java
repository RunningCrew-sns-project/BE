package com.github.accountmanagementproject.web.crews;

import com.github.accountmanagementproject.service.crews.CrewService;
import com.github.accountmanagementproject.web.dto.crews.CrewCreationRequest;
import com.github.accountmanagementproject.web.dto.responseSystem.CustomSuccessResponse;
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

    @DeleteMapping("/sendOutCrew")
    public CustomSuccessResponse sendOutCrew(@AuthenticationPrincipal String email,
                                             @RequestParam Long crewId,
                                             @RequestParam Integer outCrewId){
        return new CustomSuccessResponse.SuccessDetail()
                .message("퇴장시키기 성공")
                .responseData(crewService.sendOutCrew(email, crewId, outCrewId))
                .build();
    }
}
