package com.github.accountmanagementproject.web.controller.runJoinPost;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.exception.ResourceNotFoundException;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.repository.runningPost.RunJoinPost;
import com.github.accountmanagementproject.repository.runningPost.repository.RunJoinPostRepository;
import com.github.accountmanagementproject.service.runJoinPost.CrewRunJoinPostService;
import com.github.accountmanagementproject.service.runJoinPost.GeneralRunJoinPostService;
import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewPostSequenceResponseDto;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostCreateRequest;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralPostSequenceResponseDto;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostCreateRequest;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/join-posts/general")
public class GeneralRunJoinPostController {

    private final RunJoinPostRepository runJoinPostRepository;
    private final GeneralRunJoinPostService generalRunJoinPostService;
    private final MyUsersRepository usersRepository;
    private final AccountConfig accountConfig;


    // user 가 crew 인 경우
    @PostMapping("/{crewId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomSuccessResponse createCrewPost(
            @RequestBody @Valid GeneralRunPostCreateRequest request,
            @PathVariable Long crewId,
            @RequestParam String email) {

//        MyUser user = accountConfig.findMyUser(principal);
        MyUser user = usersRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RunJoinPost runJoinPost = generalRunJoinPostService.createGeneralPostByCrew(request, user, crewId);
        GeneralRunPostResponse responseDto = GeneralRunPostResponse.toDto(runJoinPost);

        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.CREATED)
                .message("게시물이 생성되었습니다.")
                .responseData(responseDto)
                .build();
    }


    // user 가 crew 가 아닌 경우
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomSuccessResponse createGeneralPost(
            @RequestBody @Valid GeneralRunPostCreateRequest request, @RequestParam String email) {
//        MyUser user = accountConfig.findMyUser(principal);
        MyUser user = usersRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RunJoinPost runJoinPost = generalRunJoinPostService.createGeneralPost(request, user);
        GeneralRunPostResponse responseDto = GeneralRunPostResponse.toDto(runJoinPost);

        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.CREATED)
                .message("게시물이 생성되었습니다.")
                .responseData(responseDto)
                .build();
    }


    // 게시글 상세보기
    @GetMapping("/sequence/{generalPostSequence}")
    public CustomSuccessResponse getPostByGeneralPostSequence(@PathVariable Integer generalPostSequence) {
        RunJoinPost findPost = generalRunJoinPostService.getPostByGeneralPostSequence(generalPostSequence);
        GeneralRunPostResponse responseDto = GeneralRunPostResponse.toDto(findPost);

        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.OK)
                .message("게시물이 정상 조회되었습니다.")
                .responseData(responseDto)
                .build();
    }


    // 게시글 수정
    @PostMapping("/update/{generalPostSequence}")
    public CustomSuccessResponse updatePostByGeneralPostSequence(@PathVariable Integer generalPostSequence,
                                                                 @RequestBody @Valid GeneralRunPostUpdateRequest request,
                                                                 @RequestParam String email) {
//        MyUser user = accountConfig.findMyUser(principal);
        MyUser user = usersRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        RunJoinPost updatedPost = generalRunJoinPostService.updateGeneralPost(generalPostSequence, user, request);
        GeneralRunPostResponse responseDto = GeneralRunPostResponse.toDto(updatedPost);

        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.OK)
                .message("게시물이 정상 수정되었습니다.")
                .responseData(responseDto)
                .build();
    }


    // 게시글 삭제
    @DeleteMapping("/delete/{generalPostSequence}")
    public CustomSuccessResponse deletePostByGeneralPostSequence(@PathVariable Integer generalPostSequence,
                                                                 @RequestParam String email) {
        //        MyUser user = accountConfig.findMyUser(principal);
        MyUser user = usersRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        generalRunJoinPostService.deleteGeneralPost(generalPostSequence, user);

        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.OK)
                .message("게시물이 정상 삭제되었습니다.")
                .responseData(null)
                .build();
    }


    // "일반 달리기 모집" 목록 가져오기
//    @PreAuthorize("@crewSecurityService.isUserInCrew(authentication, #crewId)") 수정 필요
//    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public CustomSuccessResponse getAll(PageRequestDto pageRequestDto) {

        PageResponseDto<GeneralPostSequenceResponseDto> response = generalRunJoinPostService.getAllGeneralPosts(pageRequestDto);

        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.OK)
                .message("모든 게시글을 조회했습니다.")
                .responseData(response)
                .build();
    }



}
