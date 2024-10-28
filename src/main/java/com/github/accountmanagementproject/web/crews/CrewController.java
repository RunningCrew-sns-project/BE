package com.github.accountmanagementproject.web.crews;

import com.github.accountmanagementproject.service.crews.CrewService;
import com.github.accountmanagementproject.web.dto.crews.CrewCreationRequest;
import com.github.accountmanagementproject.web.dto.responseSystem.CustomSuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/crews")
public class CrewController {
    private final CrewService crewService;

    @PostMapping
    public ResponseEntity<CustomSuccessResponse> createCrew(@RequestBody @Valid CrewCreationRequest request,
                                                            @AuthenticationPrincipal String email){
        crewService.crewCreation(request, email);
        CustomSuccessResponse response = new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.CREATED).message("크루 생성 성공").build();
        return new ResponseEntity<>(response, response.getSuccess().getHttpStatus());
    }

}
