package com.github.accountmanagementproject.web.controller.run;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.service.runRecords.RunRecordsService;
import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import com.github.accountmanagementproject.web.dto.runRecords.RunRecordsRequestDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/runRecords")
public class RunRecordsController implements RunRecordsControllerDocs{
    private final AccountConfig accountConfig;
    private final RunRecordsService runRecordsService;

    public RunRecordsController(AccountConfig accountConfig, RunRecordsService runRecordsService) {
        this.accountConfig = accountConfig;
        this.runRecordsService = runRecordsService;
    }

    @Override
    @GetMapping
    public CustomSuccessResponse getRunRecords(
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer cursor,
            @AuthenticationPrincipal String email){
        MyUser user = accountConfig.findMyUser(email);
        return new CustomSuccessResponse.SuccessDetail()
                .message("달리기 기록을 조회했습니다.")
                .responseData(runRecordsService.getRunRecords(size, cursor, user))
                .build();
    }

    @Override
    @PostMapping("/writeRunRecords")
    public CustomSuccessResponse postRunRecords(
            @RequestBody RunRecordsRequestDto runRecordsRequestDto,
            @AuthenticationPrincipal String email
    ){
        MyUser user = accountConfig.findMyUser(email);
        return new CustomSuccessResponse.SuccessDetail()
                .message("달리기 기록을 저장했습니다.")
                .responseData(runRecordsService.postRunRecords(runRecordsRequestDto, user))
                .build();
    }

    @Override
    @PutMapping("/modifyRunRecords")
    public CustomSuccessResponse putRunRecords(
            @RequestBody RunRecordsRequestDto runRecordsRequestDto,
            @RequestParam Integer id,
            @AuthenticationPrincipal String email
    ){
        MyUser user = accountConfig.findMyUser(email);
        return new CustomSuccessResponse.SuccessDetail()
                .message("수정을 완료했습니다.")
                .responseData(runRecordsService.putRunRecords(runRecordsRequestDto, id,  user))
                .build();
    }

    @Override
    @DeleteMapping("/deleteRunRecords")
    public CustomSuccessResponse deleteRunRecords(
            @RequestParam Integer id,
            @AuthenticationPrincipal String email
    ){
        MyUser user = accountConfig.findMyUser(email);
        return new CustomSuccessResponse.SuccessDetail()
                .message("삭제를 완료했습니다.")
                .responseData(runRecordsService.deleteRunRecords(user, id))
                .build();
    }
}
