//package com.github.accountmanagementproject.service.runJoinPost;
//
//import com.github.accountmanagementproject.exception.ResourceNotFoundException;
//import com.github.accountmanagementproject.exception.SimpleRunAppException;
//import com.github.accountmanagementproject.exception.StorageDeleteFailedException;
//import com.github.accountmanagementproject.exception.UnauthorizedException;
//import com.github.accountmanagementproject.exception.enums.ErrorCode;
//import com.github.accountmanagementproject.repository.account.user.MyUser;
//import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
//import com.github.accountmanagementproject.repository.crew.crew.Crew;
//import com.github.accountmanagementproject.repository.crew.crew.CrewsRepository;
//import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersRepository;
//import com.github.accountmanagementproject.repository.runningPost.RunJoinPost;
//import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPost;
//import com.github.accountmanagementproject.repository.runningPost.image.RunJoinPostImage;
//import com.github.accountmanagementproject.repository.runningPost.repository.RunJoinPostRepository;
//import com.github.accountmanagementproject.service.storage.StorageService;
//import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
//import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
//import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostCreateRequest;
//import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralPostSequenceResponseDto;
//import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostCreateRequest;
//import com.github.accountmanagementproject.web.dto.runJoinPost.general.GeneralRunPostUpdateRequest;
//import com.github.accountmanagementproject.web.dto.storage.FileDto;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Slice;
//import org.springframework.data.domain.SliceImpl;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.stream.Collectors;
//
//
//@Slf4j
//@RequiredArgsConstructor
//@Service
//@CacheConfig(cacheNames = "generalPosts")
//public class GeneralRunJoinPostService {
//
//    private final RunJoinPostRepository runJoinPostRepository;
//    private final MyUsersRepository userRepository;
//    private final CrewsRepository crewRepository;
//    private final CrewsUsersRepository crewsUsersRepository;
//    private final StorageService storageService;
//
//
//    /** general run post 시작 -----------------------------------------> */
//
//    @Async
//    @Transactional
//    public CompletableFuture<Void> processGeneralPostDetails(Long postId, GeneralRunPostCreateRequest request) {
//        return CompletableFuture.runAsync(() -> {
//            try {
//                RunJoinPost post = runJoinPostRepository.findById(Math.toIntExact(postId))
//                        .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND, "Post not found with ID: " + postId));
//
//                // 거리 계산
//                double calculatedDistance = GeoUtil.calculateDistance(
//                        request.getInputLatitude(), request.getInputLongitude(),
//                        request.getTargetLatitude(), request.getTargetLongitude());
//
//                // 이미지 정보가 있다면 추가
//                if (request.getFileDtos() != null && !request.getFileDtos().isEmpty()) {
//                    post.clearJoinPostImages();  // 기존 이미지를 제거하여 orphan 상태로 만듦
//
//                    for (FileDto fileDto : request.getFileDtos()) {
//                        RunJoinPostImage newImage = RunJoinPostImage.builder()
//                                .fileName(fileDto.getFileName())
//                                .imageUrl(fileDto.getFileUrl())
//                                .build();
//                        post.addJoinPostImage(newImage);  // 연관관계 편의 메서드 사용
//                    }
//                }
//                post.setDistance(calculatedDistance);
//                runJoinPostRepository.save(post);  // 변경 사항을 저장
//            } catch (Exception e) {
//                log.error("Failed to process post details: {}", e.getMessage(), e);
//            }
//        });
//    }
//
//    // (crewId 가 있는) 크루인 유저가 글 생성  TODO: 삭제 예정
//    @Transactional
//    @CacheEvict(allEntries = true)
//    public RunJoinPost createGeneralPostByCrew(GeneralRunPostCreateRequest request, MyUser user, Long crewId) {
//        Crew crew = crewRepository.findByCrewMasterUserId(user.getUserId());
//        if(!crew.getCrewId().equals(crewId)) {
//            throw new SimpleRunAppException(ErrorCode.CREW_NOT_FOUND, "Crew not found with ID: " + crewId);
//        }
//
//        // 거리 계산
//        double calculatedDistance = GeoUtil.calculateDistance(
//                request.getInputLatitude(), request.getInputLongitude(), request.getTargetLatitude(), request.getTargetLongitude());
//
//
//        RunJoinPost generalPost = GeneralRunPostCreateRequest.toEntity(request, user, crew);
//
//        // crewGeneralSequence 값을 설정
//        Integer maxGeneralPostSequence = runJoinPostRepository.findMaxGeneralPostSequenceByUserId(user.getUserId());
//
//        generalPost.setGeneralPostSequence(maxGeneralPostSequence + 1);
//        generalPost.setDistance(calculatedDistance);
//
//        // 이미지 정보가 있다면 추가
//        if (request.getFileDtos() != null && !request.getFileDtos().isEmpty()) {
//            List<RunJoinPostImage> images = request.getFileDtos().stream()
//                    .map(fileDto -> RunJoinPostImage.builder()
//                            .runJoinPost(generalPost)
//                            .fileName(fileDto.getFileName())
//                            .imageUrl(fileDto.getFileUrl())
//                            .build())
//                    .collect(Collectors.toList());
//            generalPost.setJoinPostImages(images);
//        }
//
//        return runJoinPostRepository.save(generalPost);
//    }
//
//
//    // 일반 유저가 글 생성
////    @Transactional
////    @CacheEvict(allEntries = true)
////    public RunJoinPost createGeneralPost(GeneralRunPostCreateRequest request, MyUser user) {
////
////        RunJoinPost generalPost = GeneralRunPostCreateRequest.toEntity(request, user, null);
////
////        // crewGeneralSequence 값을 설정
////        Integer maxGeneralPostSequence = runJoinPostRepository.findMaxGeneralPostSequenceByUserId(user.getUserId());
////        generalPost.setGeneralPostSequence(maxGeneralPostSequence + 1);
////
////        // 엔티티 저장 후 ID를 기반으로 비동기 작업 호출
////        RunJoinPost savedPost = runJoinPostRepository.save(generalPost);
////        processGeneralPostDetails(savedPost.getPostId(), request);
////
////        return savedPost;
////    }
//
//
//    // 게시글 상세보기
//    @Transactional(readOnly = true)
//    @Cacheable(key = "'post_' + #generalPostSequence")
//    public GeneralJoinPost getPostByRunId(Integer generalPostSequence) {
//        GeneralJoinPost crewPost = runJoinPostRepository.findByGeneralPostSequence(generalPostSequence)
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND, "Post not found with generalPostSequence: " + generalPostSequence));
//        return crewPost;
//    }
//
//    // 글 수정
//    @Transactional
//    @CacheEvict(allEntries = true)
//    public RunJoinPost updateGeneralPost(Integer generalPostSequence, MyUser user, GeneralRunPostUpdateRequest request) {
//
//        RunJoinPost crewPost = runJoinPostRepository.findByGeneralPostSequence(generalPostSequence)
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND, "Post not found with generalPostSequence: " + generalPostSequence));
//
//        if(!crewPost.getAuthor().getUserId().equals(user.getUserId())) {
//            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_EDIT, "게시물 작성자가 아닙니다. userId: " + user.getUserId());
//        }
//
//        try {
//            // 기존 이미지 URL 목록 저장 (롤백을 위해)
//            List<String> oldImageUrls = crewPost.getJoinPostImages().stream()
//                    .map(RunJoinPostImage::getImageUrl)
//                    .toList();
//
//            // 이미지 업데이트 처리
//            if (request.getFileDtos() != null) {
//                // 기존 이미지 엔티티 삭제
//                crewPost.getJoinPostImages().clear();
//
//                // 새 이미지 정보 추가
//                List<RunJoinPostImage> newImages = request.getFileDtos().stream()
//                        .map(fileDto -> RunJoinPostImage.builder()
//                                .runJoinPost(crewPost)
//                                .fileName(fileDto.getFileName())
//                                .imageUrl(fileDto.getFileUrl())
//                                .build())
//                        .toList();
//
//                crewPost.getJoinPostImages().addAll(newImages);
//            }
//
//            // 거리 재계산
//            double calculatedDistance = GeoUtil.calculateDistance(
//                    request.getInputLatitude(),
//                    request.getInputLongitude(),
//                    request.getTargetLatitude(),
//                    request.getTargetLongitude());
//
//            RunJoinPost updatedPost = request.updateEntity(crewPost, user);
//            updatedPost.setDistance(calculatedDistance);
//            updatedPost.setUpdatedAt(LocalDateTime.now());
//
//            return runJoinPostRepository.save(updatedPost);
//
//        } catch (Exception e) {
//            // 실패 시 새로 업로드된 이미지들 삭제
//            if (request.getFileDtos() != null) {
//                List<String> newImageUrls = request.getFileDtos().stream()
//                        .map(FileDto::getFileUrl)
//                        .collect(Collectors.toList());
//                storageService.uploadCancel(newImageUrls);
//            }
//            throw new SimpleRunAppException(ErrorCode.STORAGE_UPDATE_FAILED,
//                    String.format("Error while updating post: %s", e.getMessage()));
//        }
//    }
//
//
//    // 게시글 삭제
//    @Transactional
//    @CacheEvict(allEntries = true)
//    public void deleteGeneralPost(Integer generalPostSequence, MyUser user) {
//        RunJoinPost crewPost = runJoinPostRepository.findByGeneralPostSequence(generalPostSequence)
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND, "Post not found with generalPostSequence: " + generalPostSequence));
//
//
//        if(!crewPost.getAuthor().getUserId().equals(user.getUserId())) {
//            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_EDIT, "게시물 작성자가 아닙니다. userId: " + user.getUserId());
//        }
//
////        runJoinPostRepository.delete(crewPost);
//        try {
//            // S3에서 이미지 파일 삭제
//            List<String> imageUrls = crewPost.getJoinPostImages().stream()
//                    .map(RunJoinPostImage::getImageUrl)
//                    .collect(Collectors.toList());
//
//            if (!imageUrls.isEmpty()) {
//                storageService.uploadCancel(imageUrls);
//            }
//
//            runJoinPostRepository.delete(crewPost);
//
//        } catch (Exception e) {
//            log.error("게시글 삭제 중 오류 발생: {}", e.getMessage());
//            throw new SimpleRunAppException(ErrorCode.STORAGE_DELETE_FAILED,
//                    String.format("Error while deleting post: %s", e.getMessage()));
//        }
//    }
//
//
//    /**
//     * 목록 가져오기
//     *  General post 게시물만 가져오기
//     *  filter 적용
//     */
//    @Cacheable(key = "'all_' + #pageRequestDto.page + '_' + #pageRequestDto.size + '_date_' + #pageRequestDto.date + '_location_' + #pageRequestDto.location")
//    public PageResponseDto<GeneralPostSequenceResponseDto> getAllGeneralPosts(PageRequestDto pageRequestDto) {
//        Pageable pageable = pageRequestDto.getPageable();
//
//        Slice<RunJoinPost> runJoinPosts = runJoinPostRepository
//                .findPosts(pageRequestDto.getCrewId(), pageRequestDto.getDate(), pageRequestDto.getLocation(), pageable);
//
//        // generalPostSequence로 그룹화한 후, 각 그룹을 GeneralPostSequenceResponseDto로 변환
//        List<GeneralPostSequenceResponseDto> responseDtos = runJoinPosts.getContent().stream()
//                .filter(post -> post.getGeneralPostSequence() != null)  // generalPostSequence가 null이 아닌 항목만 필터링
//                .collect(Collectors.groupingBy(RunJoinPost::getGeneralPostSequence))  // generalPostSequence로 그룹화
//                .values().stream()
//                .map(GeneralPostSequenceResponseDto::new)  // 각 그룹을 GeneralPostSequenceResponseDto로 변환
//                .sorted((dto1, dto2) -> Integer.compare(dto2.getGeneralPostSequence(), dto1.getGeneralPostSequence()))  // 최신순으로 정렬
//                .collect(Collectors.toList());
//
//        // 새로운 Slice로 변환하여 반환 (hasNext는 기존 Slice의 hasNext() 사용)
//        Slice<GeneralPostSequenceResponseDto> resultSlice = new SliceImpl<>(responseDtos, pageable, runJoinPosts.hasNext());
//
//        // PageResponseDto에 변환된 Slice 결과를 감싸서 반환
//        return new PageResponseDto<>(resultSlice);
//    }
//
//
//}
