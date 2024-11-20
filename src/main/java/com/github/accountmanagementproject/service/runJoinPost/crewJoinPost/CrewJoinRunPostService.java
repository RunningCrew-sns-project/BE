package com.github.accountmanagementproject.service.runJoinPost.crewJoinPost;

import com.github.accountmanagementproject.exception.SimpleRunAppException;
import com.github.accountmanagementproject.exception.enums.ErrorCode;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.repository.crew.crew.CrewsRepository;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersRepository;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersStatus;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPostRepository;
import com.github.accountmanagementproject.repository.runningPost.crewRunGroup.CrewRunGroupRepository;
import com.github.accountmanagementproject.repository.runningPost.image.CrewJoinPostImage;
import com.github.accountmanagementproject.repository.runningPost.image.RunJoinPostImage;
import com.github.accountmanagementproject.service.runJoinPost.GeoUtil;
import com.github.accountmanagementproject.service.storage.StorageService;
import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostCreateRequest;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostResponseMapper;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostUpdateRequest;
import com.github.accountmanagementproject.web.dto.storage.FileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostCreateRequest.extractFileNameFromUrl;


@Slf4j
@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = "crewPosts")
public class CrewJoinRunPostService {

    private final CrewJoinPostRepository crewJoinPostRepository;
    private final MyUsersRepository userRepository;
    private final CrewsRepository crewRepository;
    private final CrewRunGroupRepository crewRunGroupRepository;
    private final CrewsUsersRepository crewsUsersRepository;
    private final StorageService storageService;
    private final CrewJoinPostQueryService crewJoinPostQueryService;


    /**
     * crew run post 시작 ----------------------------------------->
     */

    @Async
    @Transactional
    public CompletableFuture<Void> processPostDetails(Long crewPostId, CrewRunPostCreateRequest request) {
        return CompletableFuture.runAsync(() -> {
            try {
                CrewJoinPost post = crewJoinPostRepository.findById(crewPostId)
                        .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND, "Post not found with ID: " + crewPostId));

                post.clearJoinPostImages();  // 기존 이미지를 제거하여 orphan 상태로 만듦

                // fileUrls 처리
                if (request.getFileUrls() != null && !request.getFileUrls().isEmpty()) {
                    for (String fileUrl : request.getFileUrls()) {
                        CrewJoinPostImage image = CrewJoinPostImage.builder()
                                .fileName(extractFileNameFromUrl(fileUrl))
                                .imageUrl(fileUrl)
                                .build();
                        post.addJoinPostImage(image);  // 연관관계 편의 메서드 사용
                    }
                }

                crewJoinPostRepository.save(post);
            } catch (Exception e) {
                // 에러 발생 시 업로드된 파일 삭제
                if (request.getFileUrls() != null) {
                    storageService.uploadCancel(request.getFileUrls());
                }
                log.error("Failed to process post details: {}", e.getMessage(), e);
                throw new SimpleRunAppException(ErrorCode.IMAGE_PROCESSING_ERROR);
            }
        });
    }

    // 게시글 생성
    @Transactional
    @CacheEvict(allEntries = true)
    public CrewRunPostResponse createCrewPost(CrewRunPostCreateRequest request, MyUser user, Long crewId) {

        // 사용자 자격 확인 - 크루 마스터 또는 크루 회원으로 승인(COMPLETED) 여부 확인
        Crew crew = validateUserAndCrew(user, crewId);

        // 게시글 생성
        CrewJoinPost runJoinPost = CrewRunPostCreateRequest.toEntity(request, user, crew);

        // 거리 계산
        double calculatedDistance = calculateDistance(request);
        runJoinPost.setDistance(calculatedDistance);

        CrewJoinPost savedPost = crewJoinPostRepository.save(runJoinPost);

        // 비동기로 이미지 처리
        processPostDetails(savedPost.getCrewPostId(), request);

        return CrewRunPostResponseMapper.toDto(savedPost, crew);
    }

    private Crew validateUserAndCrew(MyUser user, Long crewId) {
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.CREW_NOT_FOUND, "Crew not found with ID: " + crewId));

        boolean isCrewMaster = crew.getCrewMaster().getUserId().equals(user.getUserId());
        boolean isCrewMember = crewsUsersRepository.existsByCrewIdAndUserIdAndStatus(
                crewId, user.getUserId(), CrewsUsersStatus.COMPLETED); // 가입 여부와 승인 상태 확인

        if (!isCrewMaster && !isCrewMember) {
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_CREATION);
        }

        return crew;
    }


    private double calculateDistance(CrewRunPostCreateRequest request) {
        return GeoUtil.calculateDistance(
                request.getInputLatitude(),
                request.getInputLongitude(),
                request.getTargetLatitude(),
                request.getTargetLongitude()
        );
    }


    // 크루 게시글 상세보기
    @Transactional(readOnly = true)
    @Cacheable(key = "'post_' + #runId")
    public CrewRunPostResponse getPostById(Long runId, MyUser user) {

        CrewJoinPost crewPost = getCrewPost(runId);

//        if (!isAuthorizedUser(crewPost.getCrew().getCrewId(), user)) {
//            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_VIEW);
//        }
        if (!isAuthorizedUser(crewPost, user)) {  // CrewJoinPost 객체 전달
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_VIEW);
        }

        Crew crew = crewRepository.findByIdWithImages(crewPost.getCrew().getCrewId())
                .orElseThrow(
                        () -> new SimpleRunAppException(ErrorCode.CREW_NOT_FOUND, "Crew not found with crewId: " + crewPost.getCrew().getCrewId()));
//        return CrewRunPostResponseMapper.toDto(crewPost, crew);
        CrewRunPostResponse response = CrewRunPostResponseMapper.toDto(crewPost, crew);
        response.setPeople(crewRunGroupRepository.countParticipantsByPostId(crewPost.getCrewPostId()));
        return response;
    }

    public boolean isAuthorizedUser(CrewJoinPost post, MyUser user) {
        // 게시글 작성자인 경우
        if (post.getAuthor().getUserId().equals(user.getUserId())) {
            return true;
        }

        // 크루 마스터이거나 크루 멤버인 경우
        return crewRepository.findById(post.getCrew().getCrewId())
                .map(crew -> crew.getCrewMaster().getUserId().equals(user.getUserId()) ||
                        crewsUsersRepository.existsByCrewIdAndUserIdAndStatus(
                                crew.getCrewId(), user.getUserId(), CrewsUsersStatus.COMPLETED))
                .orElse(false);
    }


    // 크루 글 수정
    @Transactional
    @CacheEvict(allEntries = true)
    public CrewRunPostResponse updateCrewPostByRunId(Long runId, Long crewId, MyUser user, CrewRunPostUpdateRequest request) {

        CrewJoinPost crewPost = getCrewPost(runId);

        // 권한 검증
        if (!isAuthorizedToEdit(crewPost, user)) {
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_EDIT_OR_DELETE_AUTHOR_ONLY);
        }

        try {
            handleImageUpdate(crewPost, request);  // 이미지 업데이트
            updatePostFields(crewPost, request, user);  // 게시글 필드 업데이트

            CrewJoinPost updatedPost = crewJoinPostRepository.save(crewPost);

            Crew crew = crewRepository.findByIdWithImages(crewPost.getCrew().getCrewId())
                    .orElseThrow(() -> new SimpleRunAppException(ErrorCode.CREW_NOT_FOUND));

            CrewRunPostResponse response = CrewRunPostResponseMapper.toDto(updatedPost, crew);
            response.setPeople(crewRunGroupRepository.countParticipantsByPostId(updatedPost.getCrewPostId()));
            return response;
        } catch (Exception e) {
            handleUpdateFailure(request);   // 실패 시 새로 업로드된 이미지들 삭제
            throw new SimpleRunAppException(ErrorCode.STORAGE_UPDATE_FAILED,
                    String.format("Error while deleting post: %s", e.getMessage()));
        }
    }

    // 수정 권한 확인
    private boolean isAuthorizedToEdit(CrewJoinPost post, MyUser user) {
        // 게시글 작성자인 경우
        if (post.getAuthor().getUserId().equals(user.getUserId())) {
            return true;
        }

        // 크루 마스터인 경우
        return post.getCrew().getCrewMaster().getUserId().equals(user.getUserId());
    }


    private CrewJoinPost getCrewPost(Long runId) {
        return crewJoinPostRepository.findByIdWithImages(runId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND, "Post not found with given id: " + runId));
    }

    private void validatePostAuthor(CrewJoinPost crewPost, MyUser user) {
        if (!crewPost.getAuthor().getUserId().equals(user.getUserId())) {
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_EDIT_OR_DELETE_AUTHOR_ONLY);
        }
    }

    private void handleImageUpdate(CrewJoinPost crewPost, CrewRunPostUpdateRequest request) {
        if (request.getFileUrls() != null) {
            crewPost.clearJoinPostImages();

            // 새 이미지 정보 추가
            request.getFileUrls().forEach(url -> {
                CrewJoinPostImage image = CrewJoinPostImage.builder()
                        .fileName(url)
                        .imageUrl(url)
                        .build();
                crewPost.addJoinPostImage(image);
            });
        }
    }

    private void updatePostFields(CrewJoinPost crewPost, CrewRunPostUpdateRequest request, MyUser user) {
        CrewJoinPost updatedPost = request.updateEntity(crewPost, user);

        double calculatedDistance = GeoUtil.calculateDistance(
                request.getInputLatitude(),
                request.getInputLongitude(),
                request.getTargetLatitude(),
                request.getTargetLongitude()
        );
        updatedPost.setDistance(calculatedDistance);
        updatedPost.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
    }

    private void handleUpdateFailure(CrewRunPostUpdateRequest request) {
        if (request.getFileUrls() != null) {
            storageService.uploadCancel(request.getFileUrls());
        }
    }


    // 게시글 삭제
    @Transactional
    @CacheEvict(allEntries = true)
    public void deleteCrewPostByRunId(Long runId, MyUser user, Long crewId) {
        // 사용자 권한 확인
        if (!isAuthorizedUser(crewId, user)) {
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_DELETE, "User is not authorized to delete this post.");
        }

        CrewJoinPost crewPost = getCrewPost(runId);
        validatePostAuthor(crewPost, user);

        try {
            deletePostImages(crewPost);
            crewJoinPostRepository.delete(crewPost);
        } catch (Exception e) {
            handleDeleteFailure(e);
        }

    }

    // 권한 확인하기
    public boolean isAuthorizedUser(Long crewId, MyUser user) {
        return crewRepository.findById(crewId)
                .map(crew -> crew.getCrewMaster().getUserId().equals(user.getUserId()) ||
                        crewsUsersRepository.existsByCrewIdAndUserIdAndStatus(
                                crewId, user.getUserId(), CrewsUsersStatus.COMPLETED))
                .orElse(false);
    }

    private void deletePostImages(CrewJoinPost crewPost) {
        List<String> imageUrls = crewPost.getCrewJoinPostImages().stream()
                .map(CrewJoinPostImage::getImageUrl)
                .collect(Collectors.toList());

        if (!imageUrls.isEmpty()) {
            storageService.uploadCancel(imageUrls);
        }
    }

    private void handleDeleteFailure(Exception e) {
        log.error("게시글 삭제 중 오류 발생: {}", e.getMessage());
        throw new SimpleRunAppException(ErrorCode.STORAGE_DELETE_FAILED,
                String.format("Error while deleting post: %s", e.getMessage()));
    }


    /**
     * 전체 목록 가져오기
     * filter 적용
     * @Param: size ( 페이지 당 데이터 개수 (기본값: 9) )
     * @Param : date (모임 날짜)
     * @Param : location (장소)
     * @Return 최신순 desc
     */
//    @Cacheable(key = "'crew_' + '_cursor_' + #pageRequestDto.cursor + '_user_' + #user.userId")
    public PageResponseDto<CrewRunPostResponse> getAll(PageRequestDto pageRequestDto, MyUser user) {
        int size = pageRequestDto.getSize() > 0 ? pageRequestDto.getSize() : 20;

        // findFilteredPosts 메서드에 cursor와 size 전달
        List<CrewJoinPost> crewJoinPosts = crewJoinPostRepository.findFilteredPosts(
                pageRequestDto.getDate(),
                pageRequestDto.getLocation(),
                pageRequestDto.getCursor(),
                size,
                pageRequestDto.getSortType()
        );

        // 다음 페이지 여부 판단
        boolean hasNext = crewJoinPosts.size() > pageRequestDto.getSize();
        if (hasNext) {
            crewJoinPosts.remove(crewJoinPosts.size() - 1); // 다음 페이지 확인용으로 가져온 항목 제거
        }

        // 권한 확인 후, 각 게시물에 대한 DTO 변환
        List<CrewRunPostResponse> lists = crewJoinPosts.stream()
                .map(post -> {
                    checkUserCrewMembership(user, post);  // 권한 확인

                    // 각 CrewJoinPost에 연결된 Crew 정보 가져오기
                    Crew crew = crewRepository.findByIdWithImages(post.getCrew().getCrewId())
                            .orElseThrow(() -> new SimpleRunAppException(ErrorCode.CREW_NOT_FOUND,
                                    "Crew not found for crewJoinPost"));

                    // CrewRunPostResponseMapper를 사용하여 DTO로 변환
//                    return CrewRunPostResponseMapper.toDto(post, crew);
                    CrewRunPostResponse response = CrewRunPostResponseMapper.toDto(post, crew);
                    response.setPeople(crewRunGroupRepository.countParticipantsByPostId(post.getCrewPostId()));
                    return response;
                })
                .toList();

        // 다음 커서 설정 (현재 페이지 마지막 항목의 ID 다음 항목의 ID를 커서로 설정)
        Integer nextCursor = hasNext ? lists.get(lists.size() - 1).getRunId().intValue() : null;

        return new PageResponseDto<>(lists, pageRequestDto.getSize(), !hasNext, nextCursor);
    }


    private void checkUserCrewMembership(MyUser user, CrewJoinPost post) {
        // 사용자가 크루의 승인된 멤버인지 확인
        boolean isCrewMember = crewsUsersRepository.existsByCrewsUsersPkUserUserIdAndStatus(user.getUserId(), CrewsUsersStatus.COMPLETED);

        // 사용자가 게시글 작성자인지 확인
        boolean isAuthor = post.getAuthor().getUserId().equals(user.getUserId());

        // 둘 중 하나라도 충족하지 않으면 권한 예외 발생
        if (!isCrewMember && !isAuthor) {
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_VIEW);
        }
    }

    private void checkUserCrewMembership(MyUser user) {
        boolean isCrewMember = crewsUsersRepository.existsByCrewsUsersPkUserUserIdAndStatus(user.getUserId(), CrewsUsersStatus.COMPLETED);
        if (!isCrewMember) {
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_VIEW);
        }
    }


    // DTO 로 변환
    @Transactional(readOnly = true)
    public CrewRunPostResponse getCrewPostById(Long runId) {
        CrewJoinPost crewJoinPost = crewJoinPostRepository.findById(runId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND, "Post not found with runId: " + runId));

        // 관련 크루 정보 조회 및 fetch join으로 이미지 정보까지 로드
        Crew crew = crewRepository.findByIdWithImages(crewJoinPost.getCrew().getCrewId())
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.CREW_NOT_FOUND, "Crew not found for crewJoinPost"));

        // CrewRunPostResponseMapper를 사용하여 DTO로 변환
        return CrewRunPostResponseMapper.toDto(crewJoinPost, crew);
    }


}

