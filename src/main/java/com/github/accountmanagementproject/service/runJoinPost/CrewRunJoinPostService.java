package com.github.accountmanagementproject.service.runJoinPost;

import com.github.accountmanagementproject.exception.SimpleRunAppException;
import com.github.accountmanagementproject.exception.UnauthorizedException;
import com.github.accountmanagementproject.exception.enums.ErrorCode;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.repository.crew.crew.CrewsRepository;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersRepository;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersStatus;
import com.github.accountmanagementproject.repository.runningPost.RunJoinPost;
import com.github.accountmanagementproject.repository.runningPost.image.RunJoinPostImage;
import com.github.accountmanagementproject.repository.runningPost.repository.RunJoinPostRepository;
import com.github.accountmanagementproject.service.storage.StorageService;
import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewPostSequenceResponseDto;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostCreateRequest;
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
public class CrewRunJoinPostService {

    private final RunJoinPostRepository runJoinPostRepository;
    private final MyUsersRepository userRepository;
    private final CrewsRepository crewRepository;
    private final CrewsUsersRepository crewsUsersRepository;
    private final StorageService storageService;


    /**
     * crew run post 시작 ----------------------------------------->
     */

    @Async
    @Transactional
    public CompletableFuture<Void> processPostDetails(Long postId, CrewRunPostCreateRequest request) {
        return CompletableFuture.runAsync(() -> {
            try {
                RunJoinPost post = runJoinPostRepository.findById(Math.toIntExact(postId))
                        .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND, "Post not found with ID: " + postId));

                // 거리 계산
                double calculatedDistance = GeoUtil.calculateDistance(
                        request.getInputLatitude(), request.getInputLongitude(),
                        request.getTargetLatitude(), request.getTargetLongitude());

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

                post.setDistance(calculatedDistance);
                runJoinPostRepository.save(post);
            } catch (Exception e) {
                log.error("Failed to process post details: {}", e.getMessage(), e);
            }
        });
    }


    @Transactional
    @CacheEvict(allEntries = true)
    public RunJoinPost createCrewPost(CrewRunPostCreateRequest request, MyUser user, Long crewId) {

        // 사용자 자격 확인 - 크루 마스터 또는 크루 회원으로 승인(COMPLETED) 여부 확인
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.CREW_NOT_FOUND, "Crew not found with ID: " + crewId));
        boolean isCrewMaster = crew.getCrewMaster().getUserId().equals(user.getUserId());
        boolean isCrewMember = crewsUsersRepository.existsByCrewIdAndUserIdAndStatus(
                crewId, user.getUserId(), CrewsUsersStatus.COMPLETED); // 특정 사용자가 특정 크루에 가입되어 있고, 승인된 상태인지 확인
        if (!isCrewMaster && !isCrewMember) {
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_CREATION);
        }

        // 게시글 생성
        RunJoinPost runJoinPost = CrewRunPostCreateRequest.toEntity(request, user, crew);

        // 거리 계산
        double calculatedDistance = GeoUtil.calculateDistance(
                request.getInputLatitude(), request.getInputLongitude(), request.getTargetLatitude(), request.getTargetLongitude());

        // crewPostSequence 값을 설정
        Integer maxCrewPostSequence = runJoinPostRepository.findMaxCrewPostSequenceByCrewId(crew.getCrewId());

        runJoinPost.setCrewPostSequence(maxCrewPostSequence + 1);
        runJoinPost.setDistance(calculatedDistance);

        RunJoinPost savedPost = runJoinPostRepository.save(runJoinPost);

        // 3. 비동기로 추가 정보 처리 (거리 계산, 이미지 처리)
        processPostDetails(savedPost.getPostId(), request);

        return savedPost;
    }


    // 크루 게시글 상세보기
    @Transactional(readOnly = true)
    @Cacheable(key = "'post_' + #crewPostSequence")
    public RunJoinPost getPostByCrewPostSequence(Integer crewPostSequence, MyUser user) {

        RunJoinPost crewPost = runJoinPostRepository.findByCrewPostSequence(crewPostSequence)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND, "Post not found with crewPostSequence: " + crewPostSequence));

        if (!isAuthorizedUser(crewPost.getCrew().getCrewId(), user)) {
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_VIEW);
        }
        return crewPost;
    }

    // 크루 글 수정
    @Transactional
    @CacheEvict(allEntries = true)
    public RunJoinPost updateCrewPostByCrewPostSequence(Integer crewPostSequence, Long crewId, MyUser user, CrewRunPostUpdateRequest request) {
        // 사용자 자격 확인 - 크루 마스터 또는 크루 회원으로 승인(COMPLETED) 여부 확인
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.CREW_NOT_FOUND, "Crew not found with ID: " + crewId));
        boolean isAuthorizedUser = crew.getCrewMaster().getUserId().equals(user.getUserId()) ||
                crewsUsersRepository.existsByCrewIdAndUserIdAndStatus(
                        crewId, user.getUserId(), CrewsUsersStatus.COMPLETED);
        if (!isAuthorizedUser) {
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_EDIT);
        }

        RunJoinPost crewPost = runJoinPostRepository.findByCrewPostSequence(crewPostSequence)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND, "Post not found with crewPostSequence: " + crewPostSequence));

        if (!crewPost.getAuthor().getUserId().equals(user.getUserId())) {
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_EDIT_AUTHOR_ONLY);
        }

//        // 거리 재계산
//        double calculatedDistance = this.calculateDistance(
//                request.getInputLatitude(), request.getInputLongitude(), request.getTargetLatitude(), request.getTargetLongitude());
//
//        RunJoinPost updatedPost = request.updateEntity(crewPost, crewPost.getAuthor());
//        updatedPost.setDistance(calculatedDistance);
//
//        return runJoinPostRepository.save(updatedPost);
        try {
            // 기존 이미지 URL 목록 저장 (롤백을 위해)
            List<String> oldImageUrls = crewPost.getJoinPostImages().stream()
                    .map(RunJoinPostImage::getImageUrl)
                    .toList();

            // 이미지 업데이트 처리
            if (request.getFileDtos() != null) {
                // 기존 이미지 엔티티 삭제
                crewPost.getJoinPostImages().clear();

                // 새 이미지 정보 추가
                List<RunJoinPostImage> newImages = request.getFileDtos().stream()
                        .map(fileDto -> RunJoinPostImage.builder()
                                .runJoinPost(crewPost)
                                .fileName(fileDto.getFileName())
                                .imageUrl(fileDto.getFileUrl())
                                .build())
                        .toList();

                crewPost.getJoinPostImages().addAll(newImages);
            }

            // 거리 재계산
            double calculatedDistance = GeoUtil.calculateDistance(
                    request.getInputLatitude(),
                    request.getInputLongitude(),
                    request.getTargetLatitude(),
                    request.getTargetLongitude());

            // 게시글 정보 업데이트
            RunJoinPost updatedPost = request.updateEntity(crewPost, user);
            updatedPost.setDistance(calculatedDistance);
            updatedPost.setUpdatedAt(LocalDateTime.now());

            return runJoinPostRepository.save(updatedPost);

        } catch (Exception e) {
            // 실패 시 새로 업로드된 이미지들 삭제
            if (request.getFileDtos() != null) {
                List<String> newImageUrls = request.getFileDtos().stream()
                        .map(FileDto::getFileUrl)
                        .collect(Collectors.toList());
                storageService.uploadCancel(newImageUrls);
            }
            throw new SimpleRunAppException(ErrorCode.STORAGE_UPDATE_FAILED,
                    String.format("Error while deleting post: %s", e.getMessage()));
        }
    }


    // 게시글 삭제
    @Transactional
    @CacheEvict(allEntries = true)
    public void deleteCrewPostByCrewPostSequence(Integer crewPostSequence, MyUser user, Long crewId) {
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.CREW_NOT_FOUND, "Crew not found with ID: " + crewId));
        boolean isAuthorizedUser = crew.getCrewMaster().getUserId().equals(user.getUserId()) ||
                crewsUsersRepository.existsByCrewIdAndUserIdAndStatus(
                        crewId, user.getUserId(), CrewsUsersStatus.COMPLETED);
        if (!isAuthorizedUser) {
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_DELETE);
        }

        RunJoinPost crewPost = runJoinPostRepository.findByCrewPostSequence(crewPostSequence)
                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.POST_NOT_FOUND, "Post not found with crewPostSequence: " + crewPostSequence));

        if (!crewPost.getAuthor().getUserId().equals(user.getUserId())) {
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_DELETE_AUTHOR_ONLY);
        }

//        runJoinPostRepository.delete(crewPost);
        try {
            // S3에서 이미지 파일 삭제
            List<String> imageUrls = crewPost.getJoinPostImages().stream()
                    .map(RunJoinPostImage::getImageUrl)
                    .collect(Collectors.toList());

            if (!imageUrls.isEmpty()) {
                storageService.uploadCancel(imageUrls);
            }

            // 게시글 삭제 (이미지 엔티티도 함께 삭제됨 - cascade 설정으로)
            runJoinPostRepository.delete(crewPost);

        } catch (Exception e) {
            log.error("게시글 삭제 중 오류 발생: {}", e.getMessage());
            throw new SimpleRunAppException(ErrorCode.STORAGE_DELETE_FAILED,
                    String.format("Error while deleting post: %s", e.getMessage()));
        }

    }



    /**
     * 전체 목록 가져오기
     * filter 적용
     */
    public PageResponseDto<RunJoinPost> getAll(PageRequestDto pageRequestDto) {
        // Pageable 객체 생성
        Pageable pageable = pageRequestDto.getPageable();

        Slice<RunJoinPost> crewJoinPosts = runJoinPostRepository
                .findPosts(pageRequestDto.getCrewId(), pageRequestDto.getDate(), pageRequestDto.getLocation(), pageable);

        return new PageResponseDto<>(crewJoinPosts);
    }


    /**
     * 목록 가져오기
     *  크루 게시물만 가져오기
     *  filter 적용
     */
    @Cacheable(key = "'crew_' + #pageRequestDto.crewId + '_page_' + #pageRequestDto.page + '_user_' + #user.userId")
    public PageResponseDto<CrewPostSequenceResponseDto> getAllCrewPosts(PageRequestDto pageRequestDto, MyUser user) {
        Pageable pageable = pageRequestDto.getPageable();

        Long crewId = pageRequestDto.getCrewId();
        if (crewId != null && !isAuthorizedUser(crewId, user)) {
            throw new UnauthorizedException("크루 목록을 조회할 권한이 없습니다.");
        }

        Slice<RunJoinPost> crewJoinPosts = runJoinPostRepository
                .findPosts(pageRequestDto.getCrewId(), pageRequestDto.getDate(), pageRequestDto.getLocation(), pageable);

        // RunJoinPost 목록을 CrewPostSequenceResponseDto로 변환
//        Slice<CrewPostSequenceResponseDto> responseDtos = crewJoinPosts.map(CrewPostSequenceResponseDto::new);
        // crewPostSequence로 그룹화한 후, 각 그룹을 CrewPostSequenceResponseDto로 변환
        List<CrewPostSequenceResponseDto> responseDtos = crewJoinPosts.getContent().stream()
                .filter(post -> post.getCrewPostSequence() != null)  // crewPostSequence가 null이 아닌 항목만 필터링
                .collect(Collectors.groupingBy(RunJoinPost::getCrewPostSequence))  // crewPostSequence로 그룹화
                .values().stream()
                .map(CrewPostSequenceResponseDto::new)  // 각 그룹을 CrewPostSequenceResponseDto로 변환
                .sorted((dto1, dto2) -> Integer.compare(dto2.getCrewPostSequence(), dto1.getCrewPostSequence()))  // 최신순으로 정렬
                .collect(Collectors.toList());

        // 새로운 Slice로 변환하여 반환 (hasNext는 기존 Slice의 hasNext() 사용)
        Slice<CrewPostSequenceResponseDto> resultSlice = new SliceImpl<>(responseDtos, pageable, crewJoinPosts.hasNext());

        // PageResponseDto에 변환된 Slice 결과를 감싸서 반환
        return new PageResponseDto<>(resultSlice);
    }


    // 권한 확인하기
    public boolean isAuthorizedUser(Long crewId, MyUser user) {
        return crewRepository.findById(crewId)
                .map(crew -> crew.getCrewMaster().getUserId().equals(user.getUserId()) ||
                        crewsUsersRepository.existsByCrewIdAndUserIdAndStatus(
                                crewId, user.getUserId(), CrewsUsersStatus.COMPLETED))
                .orElse(false);
    }


    public Crew findOneCrew(Integer userId) {
        return crewRepository.findByCrewMasterUserId(Long.valueOf(userId));
    }


}
