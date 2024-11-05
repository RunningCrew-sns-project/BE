package com.github.runningcrewsnsproject.web.controller.runJoinPost;

import com.github.runningcrewsnsproject.config.security.AccountConfig;
import com.github.runningcrewsnsproject.exception.ResourceNotFoundException;
import com.github.runningcrewsnsproject.repository.account.user.MyUser;
import com.github.runningcrewsnsproject.repository.account.user.MyUsersRepository;
import com.github.runningcrewsnsproject.repository.runningPost.RunJoinPost;
import com.github.runningcrewsnsproject.repository.runningPost.repository.RunJoinPostRepository;
import com.github.runningcrewsnsproject.service.runJoinPost.CrewRunJoinPostService;
import com.github.runningcrewsnsproject.web.dto.pagination.PageRequestDto;
import com.github.runningcrewsnsproject.web.dto.pagination.PageResponseDto;
import com.github.runningcrewsnsproject.web.dto.responsebuilder.CustomSuccessResponse;
import com.github.runningcrewsnsproject.web.dto.runJoinPost.crew.CrewPostSequenceResponseDto;
import com.github.runningcrewsnsproject.web.dto.runJoinPost.crew.CrewRunPostCreateRequest;
import com.github.runningcrewsnsproject.web.dto.runJoinPost.crew.CrewRunPostResponse;
import com.github.runningcrewsnsproject.web.dto.runJoinPost.crew.CrewRunPostUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

//        MyUser user = accountConfig.findMyUser(principal);  TODO: 수정 예정
        MyUser user = usersRepository.findByEmail(email)   //  TODO: 수정 예정
                .orElseThrow(() -> new ResourceNotFoundException.ExceptionBuilder()
                        .customMessage("사용자를 찾을 수 없습니다")
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
    public CustomSuccessResponse getCrewPostByCrewPostSequence(@PathVariable Integer crewPostSequence) {
        RunJoinPost findPost = crewRunJoinPostService.getPostByCrewPostSequence(crewPostSequence);
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

        crewRunJoinPostService.deleteCrewPostByCrewPostSequence(crewPostSequence, user.getUserId());

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
    public CustomSuccessResponse getAll(PageRequestDto pageRequestDto) {

        PageResponseDto<CrewPostSequenceResponseDto> response = crewRunJoinPostService.getAllCrewPosts(pageRequestDto);

        return new CustomSuccessResponse.SuccessDetail()
                .httpStatus(HttpStatus.OK)
                .message("모든 게시글을 조회했습니다.")
                .responseData(response)
                .build();
    }


}
