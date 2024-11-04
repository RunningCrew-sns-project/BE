package com.github.accountmanagementproject.web.controller.runJoinPost;

import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.exception.ResourceNotFoundException;
import com.github.accountmanagementproject.exception.SimpleRunAppException;
import com.github.accountmanagementproject.exception.enums.ErrorCode;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.repository.runningPost.RunJoinPost;
import com.github.accountmanagementproject.repository.runningPost.repository.RunJoinPostRepository;
import com.github.accountmanagementproject.service.runJoinPost.CrewRunJoinPostService;
import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
import com.github.accountmanagementproject.web.dto.responsebuilder.CustomSuccessResponse;
import com.github.accountmanagementproject.web.dto.responsebuilder.Response;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewPostSequenceResponseDto;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostCreateRequest;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostUpdateRequest;
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


    // 게시글 생성
    @PostMapping("/{crewId}/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<CrewRunPostResponse> createCrewPost(
            @RequestBody @Valid CrewRunPostCreateRequest request,
            @PathVariable Long crewId,
            @RequestParam String email) {

//        MyUser user = accountConfig.findMyUser(principal);  TODO: 수정 예정
        MyUser user = usersRepository.findByEmail(email)   //  TODO: 삭제 예정
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND, "User not found with email: " + email));

        RunJoinPost createdPost = crewRunJoinPostService.createCrewPost(request, user, crewId);
        CrewRunPostResponse crewRunPostResponse = CrewRunPostResponse.toDto(createdPost);

        return Response.success("크루 게시물이 생성되었습니다.", crewRunPostResponse);
    }


    // 상세 보기 , crewPostSequence 로 조회
//    @PreAuthorize("@crewSecurityService.isUserInCrew(authentication, #crewId)") 수정 필요
//    @PreAuthorize("isAuthenticated()")
    @GetMapping("/sequence/{crewPostSequence}")
    public Response<CrewRunPostResponse> getCrewPostByCrewPostSequence(@PathVariable Integer crewPostSequence, @RequestParam String email) {
        //        MyUser user = accountConfig.findMyUser(principal); // TODO: 수정 예정
        MyUser user = usersRepository.findByEmail(email)   //  TODO: 삭제 예정
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND, "User not found with email: " + email));

        RunJoinPost findPost = crewRunJoinPostService.getPostByCrewPostSequence(crewPostSequence, user);
        CrewRunPostResponse crewRunPostResponse = CrewRunPostResponse.toDto(findPost);

        return Response.success(HttpStatus.OK, "크루 게시물이 정상 조회되었습니다.", crewRunPostResponse);
    }


    // 글 수정
    @PostMapping("/{crewId}/update/{crewPostSequence}")
    public Response<CrewRunPostResponse> updateCrewPost(@PathVariable Long crewId, @PathVariable Integer crewPostSequence,
                                                @RequestBody @Valid CrewRunPostUpdateRequest request, @RequestParam String email) {

//        MyUser user = accountConfig.findMyUser(principal); // TODO: 수정 예정
        MyUser user = usersRepository.findByEmail(email)   //  TODO: 삭제 예정
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND, "User not found with email: " + email));

        RunJoinPost updatedPost = crewRunJoinPostService.updateCrewPostByCrewPostSequence(crewPostSequence, crewId, user, request);
        CrewRunPostResponse crewRunPostResponse = CrewRunPostResponse.toDto(updatedPost);

        return Response.success(HttpStatus.OK, "크루 게시물이 정상 수정되었습니다.", crewRunPostResponse);
    }


    // 게시글 삭제
    @DeleteMapping("/{crewId}/delete/{crewPostSequence}")
    public Response<Void> deleteCrewPost(@PathVariable Long crewId, @PathVariable Integer crewPostSequence,
                                                @RequestParam String email) {
//        MyUser user = accountConfig.findMyUser(principal);  // TODO: 수정 예정
        MyUser user = usersRepository.findByEmail(email)   //  TODO: 삭제 예정
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND, "User not found with email: " + email));

        crewRunJoinPostService.deleteCrewPostByCrewPostSequence(crewPostSequence, user, crewId);

        return Response.success(HttpStatus.OK, "게시물이 정상 삭제되었습니다.", null);
    }


    // "크루 달리기 모집" 목록 가져오기
//    @PreAuthorize("@crewSecurityService.isUserInCrew(authentication, #crewId)") 수정 필요
//    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public Response<PageResponseDto<CrewPostSequenceResponseDto>> getAll(PageRequestDto pageRequestDto, @RequestParam String email) {
        //        MyUser user = accountConfig.findMyUser(principal);  // TODO: 수정 예정
        MyUser user = usersRepository.findByEmail(email)   //  TODO: 삭제 예정
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.USER_NOT_FOUND, "User not found with email: " + email));

        PageResponseDto<CrewPostSequenceResponseDto> response = crewRunJoinPostService.getAllCrewPosts(pageRequestDto, user);

        return Response.success(HttpStatus.OK, "모든 게시물이 조회되었습니다.", response);
    }


}
