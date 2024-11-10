package com.github.accountmanagementproject.service.runJoinPost.generalJoinPost;

import com.github.accountmanagementproject.exception.SimpleRunAppException;
import com.github.accountmanagementproject.exception.enums.ErrorCode;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.repository.crew.crew.CrewsRepository;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersRepository;
import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPost;
import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPostRepository;
import com.github.accountmanagementproject.repository.runningPost.image.RunJoinPostImage;
import com.github.accountmanagementproject.service.runJoinPost.GeoUtil;
import com.github.accountmanagementproject.service.storage.StorageService;
import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostCreateRequest;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostUpdateRequest;
import com.github.accountmanagementproject.web.dto.storage.FileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
@CacheConfig(cacheNames = "generalPosts")
public class GeneralJoinRunPostService {

    private final GeneralJoinPostRepository generalJoinPostRepository;
    private final MyUsersRepository userRepository;
    private final CrewsRepository crewRepository;
    private final CrewsUsersRepository crewsUsersRepository;
    private final StorageService storageService;


    @Async
    @Transactional
    public CompletableFuture<Void> processGeneralPostDetails(Long generalPostId, GeneralRunPostCreateRequest request) {
        return CompletableFuture.runAsync(() -> {
            try {
                GeneralJoinPost post = generalJoinPostRepository.findById(generalPostId)
                        .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND, "Post not found with ID: " + generalPostId));

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
                generalJoinPostRepository.save(post);
            } catch (Exception e) {
                log.error("Failed to process post details: {}", e.getMessage(), e);
            }
        });
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public GeneralJoinPost createGeneralPost(GeneralRunPostCreateRequest request, MyUser user) {

        GeneralJoinPost generalPost = GeneralRunPostCreateRequest.toEntity(request, user);

        double calculatedDistance = calculateDistance(request);
        generalPost.setDistance(calculatedDistance);
        // 엔티티 저장 후 ID를 기반으로 비동기 작업 호출
        GeneralJoinPost savedPost = generalJoinPostRepository.save(generalPost);
        processGeneralPostDetails(savedPost.getGeneralPostId(), request);
        return savedPost;
    }


    private double calculateDistance(GeneralRunPostCreateRequest request) {
        return GeoUtil.calculateDistance(
                request.getInputLatitude(),
                request.getInputLongitude(),
                request.getTargetLatitude(),
                request.getTargetLongitude()
        );
    }


    // 게시글 상세보기
    @Transactional(readOnly = true)
    @Cacheable(key = "'post_' + #generalPostId")
    public GeneralJoinPost getPostById(Long generalPostId) {
        return getCrewPost(generalPostId);
    }

    private GeneralJoinPost getCrewPost(Long generalPostId) {
        return generalJoinPostRepository.findByIdWithImages(generalPostId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND, "Post not found with runId: " + generalPostId));
    }


    // 글 수정
    @Transactional
    @CachePut(key = "'post_' + #generalPostId")  // 또는 @CacheEvict(key = "'post_' + #generalPostId")
    public GeneralJoinPost updateGeneralPost(Long generalPostId, MyUser user, GeneralRunPostUpdateRequest request) {
        GeneralJoinPost generalPost = getCrewPost(generalPostId);
        validateGeneralPostAuthor(generalPost, user);

        try {
            handlePostImageUpdate(generalPost, request);  // 이미지 업데이트
            updateGeneralPostFields(generalPost, request, user);  // 게시글 필드 업데이트
            return generalJoinPostRepository.save(generalPost);
        } catch (Exception e) {
            handleImageUpdateFailure(request);   // 실패 시 새로 업로드된 이미지들 삭제
            throw new SimpleRunAppException(ErrorCode.STORAGE_UPDATE_FAILED,
                    String.format("Error while deleting post: %s", e.getMessage()));
        }

    }

    private void validateGeneralPostAuthor(GeneralJoinPost generalPost, MyUser user) {
        if (!generalPost.getAuthor().getUserId().equals(user.getUserId())) {
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_EDIT_OR_DELETE_AUTHOR_ONLY);
        }
    }

    private void handlePostImageUpdate(GeneralJoinPost generalPost, GeneralRunPostUpdateRequest request) {
        if (request.getFileDtos() != null) {
            // 기존 이미지 엔티티 삭제
            generalPost.getGeneralJoinPostImages().clear();

            // 새 이미지 정보 추가
            List<RunJoinPostImage> newImages = request.getFileDtos().stream()
                    .map(fileDto -> RunJoinPostImage.builder()
                            .generalJoinPost(generalPost)
                            .fileName(fileDto.getFileName())
                            .imageUrl(fileDto.getFileUrl())
                            .build())
                    .toList();

            generalPost.getGeneralJoinPostImages().addAll(newImages);
        }
    }

    private void updateGeneralPostFields(GeneralJoinPost generalPost, GeneralRunPostUpdateRequest request, MyUser user) {
        if (user == null || user.getUserId() == null) {
            throw new SimpleRunAppException(ErrorCode.USER_NOT_FOUND, "User is null or has no ID.");
        }
        GeneralJoinPost updatedPost = request.updateEntity(generalPost, user);

        double calculatedDistance = GeoUtil.calculateDistance(
                request.getInputLatitude(),
                request.getInputLongitude(),
                request.getTargetLatitude(),
                request.getTargetLongitude()
        );
        updatedPost.setDistance(calculatedDistance);
        updatedPost.setUpdatedAt(LocalDateTime.now());
    }

    private void handleImageUpdateFailure(GeneralRunPostUpdateRequest request) {
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
    public void deleteGeneralPost(Long generalPostId, MyUser user) {
        GeneralJoinPost generalPost = getCrewPost(generalPostId);
        validateGeneralPostAuthor(generalPost, user);

        try {
            deletePostImages(generalPost);
            generalJoinPostRepository.delete(generalPost);
        } catch (Exception e) {
            handleDeleteFailure(e);
        }
    }

    private void deletePostImages(GeneralJoinPost generalPost) {
        List<String> imageUrls = generalPost.getGeneralJoinPostImages().stream()
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
    @Cacheable(key = "'general_' + '_cursor_' + #pageRequestDto.cursor")
    public PageResponseDto<GeneralRunPostResponse> getAll(PageRequestDto pageRequestDto) {
        // findFilteredPosts 메서드에 cursor와 size 전달
        List<GeneralJoinPost> joinPosts = generalJoinPostRepository.findFilteredPosts(
                pageRequestDto.getDate(),
                pageRequestDto.getLocation(),
                pageRequestDto.getCursor(),
                pageRequestDto.getSize()
        );

        // 다음 페이지 여부 판단
        boolean hasNext = joinPosts.size() > pageRequestDto.getSize();
        if (hasNext) {
            joinPosts.remove(joinPosts.size() - 1); // 다음 페이지 확인용으로 가져온 항목 제거
        }

        // 각 게시물을 DTO로 변환
        List<GeneralRunPostResponse> lists = joinPosts.stream()
                .map(GeneralRunPostResponse::toDto)
                .toList();

        // 다음 커서 설정 (마지막 요소의 ID를 커서로 설정)
        Integer nextCursor = hasNext ? lists.get(lists.size() - 1).getRunId().intValue() : null;

        // PageResponseDto 객체로 반환
        return new PageResponseDto<>(lists, pageRequestDto.getSize(), !hasNext, nextCursor);
    }


}
