package com.github.accountmanagementproject.service.runJoinPost;

import com.github.accountmanagementproject.exception.ResourceNotFoundException;
import com.github.accountmanagementproject.exception.StorageDeleteFailedException;
import com.github.accountmanagementproject.exception.UnauthorizedException;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.repository.crew.crew.CrewsRepository;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersRepository;
import com.github.accountmanagementproject.repository.runningPost.RunJoinPost;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersStatus;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class CrewRunJoinPostService {

    private final RunJoinPostRepository runJoinPostRepository;
    private final MyUsersRepository userRepository;
    private final CrewsRepository crewRepository;
    private final CrewsUsersRepository crewsUsersRepository;
    private final StorageService storageService;


    /** crew run post 시작 -----------------------------------------> */

    @Transactional
    public RunJoinPost createCrewPost(CrewRunPostCreateRequest request, MyUser user, Long crewId) { // TODO : 비동기 처리
        // 사용자 자격 확인 - 크루 마스터 또는 크루 회원으로 승인(COMPLETED) 여부 확인
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new ResourceNotFoundException.ExceptionBuilder()
                        .customMessage("크루를 찾을 수 없습니다.")
                        .systemMessage("Crew does not exist with id: " + crewId)
                        .request("crewId: " + crewId)
                        .build());

        boolean isCrewMaster = crew.getCrewMaster().getUserId().equals(user.getUserId());
        boolean isCrewMember = crewsUsersRepository.existsByCrewIdAndUserIdAndStatus(
                crewId, user.getUserId(), CrewsUsersStatus.COMPLETED); // 특정 사용자가 특정 크루에 가입되어 있고, 승인된 상태인지 확인

        if (!isCrewMaster && !isCrewMember) {
            throw new UnauthorizedException("게시글을 작성할 권한이 없습니다.");
        }

        // 게시글 생성
        RunJoinPost runJoinPost = CrewRunPostCreateRequest.toEntity(request, user, crew);

        // 거리 계산
        double calculatedDistance = this.calculateDistance(
                request.getInputLatitude(), request.getInputLongitude(), request.getTargetLatitude(), request.getTargetLongitude());

        // crewPostSequence 값을 설정
        Integer maxCrewPostSequence = runJoinPostRepository.findMaxCrewPostSequenceByCrewId(crew.getCrewId());

        runJoinPost.setCrewPostSequence(maxCrewPostSequence + 1);
        runJoinPost.setDistance(calculatedDistance);

        // 이미지 정보가 있다면 추가
        if (request.getFileDtos() != null && !request.getFileDtos().isEmpty()) {
            List<RunJoinPostImage> images = request.getFileDtos().stream()
                    .map(fileDto -> RunJoinPostImage.builder()
                            .runJoinPost(runJoinPost)
                            .fileName(fileDto.getFileName())
                            .imageUrl(fileDto.getFileUrl())
                            .build())
                    .collect(Collectors.toList());
            runJoinPost.setJoinPostImages(images);
        }

        runJoinPostRepository.save(runJoinPost);
        return runJoinPost;
    }

    // 크루 게시글 상세보기
    @Transactional(readOnly = true)
    public RunJoinPost getPostByCrewPostSequence(Integer crewPostSequence, MyUser user) {

        RunJoinPost crewPost = runJoinPostRepository.findByCrewPostSequence(crewPostSequence)
                .orElseThrow(() -> new ResourceNotFoundException.ExceptionBuilder()
                        .customMessage("게시글을 찾을 수 없습니다")
                        .systemMessage("Post not found with crewPostSequence: " + crewPostSequence)
                        .request("crewPostSequence: " + crewPostSequence)
                        .build());

        if (!isAuthorizedUser(crewPost.getCrew().getCrewId(), user)) {
            throw new UnauthorizedException("해당 게시물을 조회할 권한이 없습니다.");
        }
        return crewPost;
    }

    // 크루 글 수정
    @Transactional
    public RunJoinPost updateCrewPostByCrewPostSequence(Integer crewPostSequence, Long crewId, MyUser user, CrewRunPostUpdateRequest request) {

        // 사용자 자격 확인 - 크루 마스터 또는 크루 회원으로 승인(COMPLETED) 여부 확인
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new ResourceNotFoundException.ExceptionBuilder()
                        .customMessage("크루를 찾을 수 없습니다.")
                        .systemMessage("Crew does not exist with id: " + crewId)
                        .request("crewId: " + crewId)
                        .build());

        boolean isAuthorizedUser = crew.getCrewMaster().getUserId().equals(user.getUserId()) ||
                crewsUsersRepository.existsByCrewIdAndUserIdAndStatus(
                        crewId, user.getUserId(), CrewsUsersStatus.COMPLETED);

        if (!isAuthorizedUser) {
            throw new UnauthorizedException("게시글을 수정할 권한이 없습니다.");
        }

        RunJoinPost crewPost = runJoinPostRepository.findByCrewPostSequence(crewPostSequence)
                .orElseThrow(() -> new ResourceNotFoundException.ExceptionBuilder()
                        .customMessage("게시글을 찾을 수 없습니다")
                        .systemMessage("Post not found with crewPostSequence: " + crewPostSequence)
                        .request("crewPostSequence: " + crewPostSequence)
                        .build()
                );

        if (!crewPost.getAuthor().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedException("게시글 작성자만 수정할 수 있습니다.");
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
            // 기존 이미지 URL 목록 저장 (롤백 시 사용)
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
            double calculatedDistance = this.calculateDistance(
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
            throw e;
        }
    }


    // 게시글 삭제
    @Transactional
    public void deleteCrewPostByCrewPostSequence(Integer crewPostSequence, MyUser user, Long crewId) {
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new ResourceNotFoundException.ExceptionBuilder()
                        .customMessage("크루를 찾을 수 없습니다.")
                        .systemMessage("Crew does not exist with id: " + crewId)
                        .request("crewId: " + crewId)
                        .build());

        boolean isAuthorizedUser = crew.getCrewMaster().getUserId().equals(user.getUserId()) ||
                crewsUsersRepository.existsByCrewIdAndUserIdAndStatus(
                        crewId, user.getUserId(), CrewsUsersStatus.COMPLETED);

        if (!isAuthorizedUser) {
            throw new UnauthorizedException("게시글을 삭제할 권한이 없습니다.");
        }

        RunJoinPost crewPost = runJoinPostRepository.findByCrewPostSequence(crewPostSequence)
                .orElseThrow(() -> new ResourceNotFoundException.ExceptionBuilder()
                        .customMessage("게시글을 찾을 수 없습니다")
                        .systemMessage("Post not found with crewPostSequence: " + crewPostSequence)
                        .request("crewPostSequence: " + crewPostSequence)
                        .build());

        if (!crewPost.getAuthor().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedException("게시글 작성자만 삭제할 수 있습니다.");
        }

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
            throw new StorageDeleteFailedException.ExceptionBuilder()
                    .customMessage("게시글 삭제 중 오류가 발생했습니다")
                    .systemMessage("Error while deleting post: " + e.getMessage())
                    .request("") // 또는 관련 식별자
                    .build();
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


    // 거리계산 Haversine formula
    // double lat1, double lon1 : 시작위치,   double lat2, double lon2 : 종료위치
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double earthRadius = 6371; //Kilometers
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    }

}
