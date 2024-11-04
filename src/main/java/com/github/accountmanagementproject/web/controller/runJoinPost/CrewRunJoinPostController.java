package com.github.accountmanagementproject.web.controller.runJoinPost;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.exception.ResourceNotFoundException;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.repository.runningPost.repository.RunJoinPostRepository;
import com.github.accountmanagementproject.repository.runningPost.RunJoinPost;
import com.github.accountmanagementproject.service.runJoinPost.CrewRunJoinPostService;
import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.RunJoinPostResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewPostSequenceResponseDto;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostCreateRequest;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/join-posts/crews")
public class CrewRunJoinPostController {

    private final RunJoinPostRepository runJoinPostRepository;
    private final CrewRunJoinPostService crewRunJoinPostService;
    private final MyUsersRepository usersRepository;
    private final AccountConfig accountConfig;


    // Test
//    @GetMapping("/get-all")
//    public ResponseEntity<?> getAll() {
//
//        List<RunJoinPost> runJoinPosts = runJoinPostRepository.findAll();
//        List<RunJoinPostDto> dtos = runJoinPosts.stream()
//                .map(RunJoinPostDto::toDto)  // 엔티티를 DTO로 변환
//                .toList();
//        return ResponseEntity.ok().body(dtos);
//    }


    /** crew run post 시작 -----------------------------------------> */
    @PostMapping("/{crewId}/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomSuccessResponse createCrewPost(
            @RequestBody @Valid CrewRunPostCreateRequest request,
            @PathVariable Long crewId,
            @RequestParam String email) {
                                            // TODO : 비동기 처리
//        MyUser user = accountConfig.findMyUser(principal);  TODO: 추가 예정
        MyUser user = usersRepository.findByEmail(email)   //  TODO: 삭제 예정
                .orElseThrow(() -> new ResourceNotFoundException.ExceptionBuilder()
                        .customMessage("User 를 찾을 수 없습니다")
                        .systemMessage("User not found with email: " + email)
                        .request("email: " + email)
                        .build()
                );

        RunJoinPost createdPost = crewRunJoinPostService.createCrewPost(request, user, crewId);
        CrewRunPostResponse crewRunPostResponse = CrewRunPostResponse.toDto(createdPost);

        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.CREATED)
                .message("크루 게시물이 생성되었습니다.")
                .responseData(crewRunPostResponse)
                .build();
    }


    // 상세 보기 , crewPostSequence 로 조회
//    @PreAuthorize("@crewSecurityService.isUserInCrew(authentication, #crewId)") 수정 필요
//    @PreAuthorize("isAuthenticated()")
    @GetMapping("/sequence/{crewPostSequence}")
    public CustomSuccessResponse getCrewPostByCrewPostSequence(@PathVariable Integer crewPostSequence, @RequestParam String email) {
        //        MyUser user = accountConfig.findMyUser(principal); // TODO: 수정 예정
        MyUser user = usersRepository.findByEmail(email)    // TODO: 수정 예정
                .orElseThrow(() -> new ResourceNotFoundException.ExceptionBuilder()
                        .customMessage("사용자를 찾을 수 없습니다")
                        .systemMessage("User not found with email: " + email)
                        .request("email: " + email)
                        .build()
                );

        RunJoinPost findPost = crewRunJoinPostService.getPostByCrewPostSequence(crewPostSequence, user);
        CrewRunPostResponse crewRunPostResponse = CrewRunPostResponse.toDto(findPost);

        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.OK)
                .message("크루 게시물이 정상 조회되었습니다.")
                .responseData(crewRunPostResponse)
                .build();
    }


    // 글 수정
    @PostMapping("/{crewId}/update/{crewPostSequence}")
    public CustomSuccessResponse updateCrewPost(@PathVariable Long crewId, @PathVariable Integer crewPostSequence,
                                                @RequestBody @Valid CrewRunPostUpdateRequest request, @RequestParam String email) {

//        MyUser user = accountConfig.findMyUser(principal); // TODO: 수정 예정
        MyUser user = usersRepository.findByEmail(email)    // TODO: 수정 예정
                .orElseThrow(() -> new ResourceNotFoundException.ExceptionBuilder()
                        .customMessage("사용자를 찾을 수 없습니다")
                        .systemMessage("User not found with email: " + email)
                        .request("email: " + email)
                        .build()
                );

        RunJoinPost updatedPost = crewRunJoinPostService.updateCrewPostByCrewPostSequence(crewPostSequence, crewId, user, request);
        CrewRunPostResponse crewRunPostResponse = CrewRunPostResponse.toDto(updatedPost);
        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.OK)
                .message("크루 게시물이 정상 수정되었습니다.")
                .responseData(crewRunPostResponse)
                .build();
    }


    // 게시글 삭제
    @DeleteMapping("/{crewId}/delete/{crewPostSequence}")
    public CustomSuccessResponse deleteCrewPost(@PathVariable Long crewId, @PathVariable Integer crewPostSequence,
                                                @RequestParam String email) {
//        MyUser user = accountConfig.findMyUser(principal);  // TODO: 수정 예정
        MyUser user = usersRepository.findByEmail(email)      // TODO: 수정 예정
                .orElseThrow(() -> new ResourceNotFoundException.ExceptionBuilder()
                        .customMessage("사용자를 찾을 수 없습니다")
                        .systemMessage("User not found with email: " + email)
                        .request("email: " + email)
                        .build()
                );

        crewRunJoinPostService.deleteCrewPostByCrewPostSequence(crewPostSequence, user, crewId);

        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.OK)
                .message("크루 게시물이 정상 삭제되었습니다.")
                .responseData(null)
                .build();
    }


    // "크루 달리기 모집" 목록 가져오기
//    @PreAuthorize("@crewSecurityService.isUserInCrew(authentication, #crewId)") 수정 필요
//    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public CustomSuccessResponse getAll(PageRequestDto pageRequestDto, @RequestParam String email) {
        //        MyUser user = accountConfig.findMyUser(principal);  // TODO: 수정 예정
        MyUser user = usersRepository.findByEmail(email)      // TODO: 수정 예정
                .orElseThrow(() -> new ResourceNotFoundException.ExceptionBuilder()
                        .customMessage("사용자를 찾을 수 없습니다")
                        .systemMessage("User not found with email: " + email)
                        .request("email: " + email)
                        .build()
                );

        PageResponseDto<CrewPostSequenceResponseDto> response = crewRunJoinPostService.getAllCrewPosts(pageRequestDto, user);

        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.OK)
                .message("모든 게시글을 조회했습니다.")
                .responseData(response)
                .build();
    }


}
