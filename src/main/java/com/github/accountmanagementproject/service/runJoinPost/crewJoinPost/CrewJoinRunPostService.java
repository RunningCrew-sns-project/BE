package com.github.accountmanagementproject.service.runJoinPost.crewJoinPost;

import com.github.accountmanagementproject.exception.SimpleRunAppException;
import com.github.accountmanagementproject.exception.enums.ErrorCode;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.repository.crew.crew.CrewsRepository;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersRepository;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersStatus;
import com.github.accountmanagementproject.repository.runningPost.RunJoinPost;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPostRepository;
import com.github.accountmanagementproject.repository.runningPost.image.RunJoinPostImage;
import com.github.accountmanagementproject.service.runJoinPost.GeoUtil;
import com.github.accountmanagementproject.service.storage.StorageService;
import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;

import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostCreateRequest;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostUpdateRequest;
import com.github.accountmanagementproject.web.dto.storage.FileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = "crewPosts")
public class CrewJoinRunPostService {

    private final CrewJoinPostRepository crewJoinPostRepository;
    private final MyUsersRepository userRepository;
    private final CrewsRepository crewRepository;
    private final CrewsUsersRepository crewsUsersRepository;
    private final StorageService storageService;
    private final CrewJoinPostQueryService crewJoinPostQueryService;


    /**
     * crew run post 시작 ----------------------------------------->
     */

    @Async
    @Transactional
    public CompletableFuture<Void> processPostDetails(Long runId, CrewRunPostCreateRequest request) {
        return CompletableFuture.runAsync(() -> {
            try {
                CrewJoinPost post = crewJoinPostRepository.findById(runId)
                        .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND, "Post not found with ID: " + runId));

                // 이미지 처리
                if (request.getFileDtos() != null && !request.getFileDtos().isEmpty()) {
                    post.clearJoinPostImages();  // 기존 이미지를 제거하여 orphan 상태로 만듦

                    // 새로운 이미지 DTO를 순회하며 엔티티를 생성하고 기존 컬렉션에 추가
                    for (FileDto fileDto : request.getFileDtos()) {
                        RunJoinPostImage newImage = RunJoinPostImage.builder()
                                .fileName(fileDto.getFileName())
                                .imageUrl(fileDto.getFileUrl())
                                .build();
                        post.addJoinPostImage(newImage);  // 연관관계 편의 메서드 사용
                    }
                }
                crewJoinPostRepository.save(post);
            } catch (Exception e) {
                log.error("Failed to process post details: {}", e.getMessage(), e);
            }
        });
    }

    // 게시글 생성
    @Transactional
    @CacheEvict(allEntries = true)
    public CrewJoinPost createCrewPost(CrewRunPostCreateRequest request, MyUser user, Long crewId) {

        // 사용자 자격 확인 - 크루 마스터 또는 크루 회원으로 승인(COMPLETED) 여부 확인
        Crew crew = validateUserAndCrew(user, crewId);

        // 게시글 생성
        CrewJoinPost runJoinPost = CrewRunPostCreateRequest.toEntity(request, user, crew);

        // 거리 계산
        double calculatedDistance = calculateDistance(request);
        runJoinPost.setDistance(calculatedDistance);

        CrewJoinPost savedPost = crewJoinPostRepository.save(runJoinPost);

        // 비동기로 이미지 처리
        processPostDetails(savedPost.getRunId(), request);

        return savedPost;
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
    public CrewJoinPost getPostById(Long runId, MyUser user) {

        CrewJoinPost crewPost = getCrewPost(runId);

        if (!isAuthorizedUser(crewPost.getCrew().getCrewId(), user)) {
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_VIEW);
        }
        return crewPost;
    }


    // 크루 글 수정
    @Transactional
    @CacheEvict(allEntries = true)
    public CrewJoinPost updateCrewPostByRunId(Long runId, Long crewId, MyUser user, CrewRunPostUpdateRequest request) {

        Crew crew = validateUserAndCrew(user, crewId);
        CrewJoinPost crewPost = getCrewPost(runId);
        validatePostAuthor(crewPost, user);

        try {
            handleImageUpdate(crewPost, request);  // 이미지 업데이트
            updatePostFields(crewPost, request, user);  // 게시글 필드 업데이트
            return crewJoinPostRepository.save(crewPost);
        } catch (Exception e) {
            handleUpdateFailure(request);   // 실패 시 새로 업로드된 이미지들 삭제
            throw new SimpleRunAppException(ErrorCode.STORAGE_UPDATE_FAILED,
                    String.format("Error while deleting post: %s", e.getMessage()));
        }
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
        if (request.getFileDtos() != null) {
            // 기존 이미지 엔티티 삭제
            crewPost.getCrewJoinPostImages().clear();

            // 새 이미지 정보 추가
            List<RunJoinPostImage> newImages = request.getFileDtos().stream()
                    .map(fileDto -> RunJoinPostImage.builder()
                            .crewJoinPost(crewPost)
                            .fileName(fileDto.getFileName())
                            .imageUrl(fileDto.getFileUrl())
                            .build())
                    .toList();

            crewPost.getCrewJoinPostImages().addAll(newImages);
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
        updatedPost.setUpdatedAt(LocalDateTime.now());
    }

    private void handleUpdateFailure(CrewRunPostUpdateRequest request) {
        if (request.getFileDtos() != null) {
            List<String> newImageUrls = request.getFileDtos().stream()
                    .map(FileDto::getFileUrl)
                    .collect(Collectors.toList());
            storageService.uploadCancel(newImageUrls);
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
                .map(RunJoinPostImage::getImageUrl)
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
    @Cacheable(key = "'crew_' + '_page_' + #pageRequestDto.page + '_user_' + #user.userId")
    public PageResponseDto<CrewRunPostResponse> getAll(PageRequestDto pageRequestDto, MyUser user) {

        Pageable pageable = pageRequestDto.getPageable();

        long totalCount = crewJoinPostRepository.countAll();
        log.debug("Total posts count: {}", totalCount);

        Slice<CrewJoinPost> crewJoinPosts = crewJoinPostRepository  // 권한 확인 후, 게시물 조회
                .findFilteredPosts(pageRequestDto.getDate(), pageRequestDto.getLocation(), pageable);
        log.debug("Found posts size: {}", crewJoinPosts.getContent().size());

        crewJoinPosts.forEach(post -> checkUserCrewMembership(user, post));

        List<CrewRunPostResponse> lists = crewJoinPosts.stream()
                .map(CrewRunPostResponse::toDto).toList();
        Slice<CrewRunPostResponse> responseSlice = new SliceImpl<>(lists, pageable, crewJoinPosts.hasNext());

        return new PageResponseDto<>(responseSlice);
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



//    public Crew findOneCrew(Integer userId) {
//        return crewRepository.findByCrewMasterUserId(Long.valueOf(userId));
//    }


}
