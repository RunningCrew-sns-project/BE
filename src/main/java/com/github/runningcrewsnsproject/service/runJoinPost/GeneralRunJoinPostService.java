package com.github.runningcrewsnsproject.service.runJoinPost;

import com.github.runningcrewsnsproject.exception.ResourceNotFoundException;
import com.github.runningcrewsnsproject.exception.StorageDeleteFailedException;
import com.github.runningcrewsnsproject.exception.UnauthorizedException;
import com.github.runningcrewsnsproject.repository.account.user.MyUser;
import com.github.runningcrewsnsproject.repository.account.user.MyUsersRepository;
import com.github.runningcrewsnsproject.repository.crew.crew.Crew;
import com.github.runningcrewsnsproject.repository.crew.crew.CrewsRepository;
import com.github.runningcrewsnsproject.repository.crew.crewuser.CrewsUsersRepository;
import com.github.runningcrewsnsproject.repository.runningPost.RunJoinPost;
import com.github.runningcrewsnsproject.repository.runningPost.image.RunJoinPostImage;
import com.github.runningcrewsnsproject.repository.runningPost.repository.RunJoinPostRepository;
import com.github.runningcrewsnsproject.service.storage.StorageService;
import com.github.runningcrewsnsproject.web.dto.pagination.PageRequestDto;
import com.github.runningcrewsnsproject.web.dto.pagination.PageResponseDto;
import com.github.runningcrewsnsproject.web.dto.runJoinPost.general.GeneralPostSequenceResponseDto;
import com.github.runningcrewsnsproject.web.dto.runJoinPost.general.GeneralRunPostCreateRequest;
import com.github.runningcrewsnsproject.web.dto.runJoinPost.general.GeneralRunPostUpdateRequest;
import com.github.runningcrewsnsproject.web.dto.storage.FileDto;
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
public class GeneralRunJoinPostService {

    private final RunJoinPostRepository runJoinPostRepository;
    private final MyUsersRepository userRepository;
    private final CrewsRepository crewRepository;
    private final CrewsUsersRepository crewsUsersRepository;
    private final StorageService storageService;


    /** general run post 시작 -----------------------------------------> */


    // (crewId 가 있는) 크루인 유저가 글 생성
    @Transactional
    public RunJoinPost createGeneralPostByCrew(GeneralRunPostCreateRequest request, MyUser user, Long crewId) {
        Crew crew = crewRepository.findByCrewMasterUserId(user.getUserId());
        if(!crew.getCrewId().equals(crewId)) {
            throw new ResourceNotFoundException.ExceptionBuilder()
                    .customMessage("크루를 찾을 수 없습니다")
                    .systemMessage("Crew does not exist")
                    .request("crewId: " + crewId)
                    .build();
        }

        // 거리 계산
        double calculatedDistance = this.calculateDistance(
                request.getInputLatitude(), request.getInputLongitude(), request.getTargetLatitude(), request.getTargetLongitude());


        RunJoinPost generalPost = GeneralRunPostCreateRequest.toEntity(request, user, crew);

        // crewGeneralSequence 값을 설정
        Integer maxGeneralPostSequence = runJoinPostRepository.findMaxGeneralPostSequenceByUserId(user.getUserId());

        generalPost.setGeneralPostSequence(maxGeneralPostSequence + 1);
        generalPost.setDistance(calculatedDistance);

        // 이미지 정보가 있다면 추가
        if (request.getFileDtos() != null && !request.getFileDtos().isEmpty()) {
            List<RunJoinPostImage> images = request.getFileDtos().stream()
                    .map(fileDto -> RunJoinPostImage.builder()
                            .runJoinPost(generalPost)
                            .fileName(fileDto.getFileName())
                            .imageUrl(fileDto.getFileUrl())
                            .build())
                    .collect(Collectors.toList());
            generalPost.setJoinPostImages(images);
        }

        return runJoinPostRepository.save(generalPost);
    }


    // 일반 유저가 글 생성
    @Transactional
    public RunJoinPost createGeneralPost(GeneralRunPostCreateRequest request, MyUser user) {
        // 거리 계산
        double calculatedDistance = this.calculateDistance(
                request.getInputLatitude(), request.getInputLongitude(), request.getTargetLatitude(), request.getTargetLongitude());

        RunJoinPost generalPost = GeneralRunPostCreateRequest.toEntity(request, user, null);

        // crewGeneralSequence 값을 설정
        Integer maxGeneralPostSequence = runJoinPostRepository.findMaxGeneralPostSequenceByUserId(user.getUserId());

        generalPost.setGeneralPostSequence(maxGeneralPostSequence + 1);
        generalPost.setDistance(calculatedDistance);

        // 이미지 정보가 있다면 추가
        if (request.getFileDtos() != null && !request.getFileDtos().isEmpty()) {
            List<RunJoinPostImage> images = request.getFileDtos().stream()
                    .map(fileDto -> RunJoinPostImage.builder()
                            .runJoinPost(generalPost)
                            .fileName(fileDto.getFileName())
                            .imageUrl(fileDto.getFileUrl())
                            .build())
                    .collect(Collectors.toList());
            generalPost.setJoinPostImages(images);
        }

        return runJoinPostRepository.save(generalPost);
    }


    // 게시글 상세보기
    @Transactional(readOnly = true)
    public RunJoinPost getPostByGeneralPostSequence(Integer generalPostSequence) {
        RunJoinPost crewPost = runJoinPostRepository.findByGeneralPostSequence(generalPostSequence)
                .orElseThrow(() -> new ResourceNotFoundException.ExceptionBuilder()
                        .customMessage("게시글을 찾을 수 없습니다")
                        .systemMessage("Post not found with generalPostSequence: " + generalPostSequence)
                        .request("generalPostSequence: " + generalPostSequence)
                        .build());
        return crewPost;
    }

    // 글 수정
    @Transactional
    public RunJoinPost updateGeneralPost(Integer generalPostSequence, MyUser user, GeneralRunPostUpdateRequest request) {

        RunJoinPost crewPost = runJoinPostRepository.findByGeneralPostSequence(generalPostSequence)
                .orElseThrow(() -> new ResourceNotFoundException.ExceptionBuilder()
                        .customMessage("게시글을 찾을 수 없습니다")
                        .systemMessage("Post not found with generalPostSequence: " + generalPostSequence)
                        .request("crewPostSequence: " + generalPostSequence)
                        .build());

        if(!crewPost.getAuthor().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedException("게시글 작성자가 아닙니다. 수정 권한이 없습니다.");
        }

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
    public void deleteGeneralPost(Integer generalPostSequence, MyUser user) {
        RunJoinPost crewPost = runJoinPostRepository.findByGeneralPostSequence(generalPostSequence)
                .orElseThrow(() -> new ResourceNotFoundException.ExceptionBuilder()
                        .customMessage("게시글을 찾을 수 없습니다")
                        .systemMessage("Post not found with generalPostSequence: " + generalPostSequence)
                        .request("generalPostSequence: " + generalPostSequence)
                        .build());

        if(!crewPost.getAuthor().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedException("게시글 작성자가 아닙니다. 수정 권한이 없습니다.");
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
            throw new StorageDeleteFailedException.ExceptionBuilder()
                    .customMessage("게시글 삭제 중 오류가 발생했습니다")
                    .systemMessage("Error while deleting post: " + e.getMessage())
                    .request("") // 또는 관련 식별자
                    .build();
        }
    }


    /**
     * 목록 가져오기
     *  General post 게시물만 가져오기
     *  filter 적용
     *  TODO: 이미지 response
     */
    public PageResponseDto<GeneralPostSequenceResponseDto> getAllGeneralPosts(PageRequestDto pageRequestDto) {
        Pageable pageable = pageRequestDto.getPageable();

        Slice<RunJoinPost> runJoinPosts = runJoinPostRepository
                .findPosts(pageRequestDto.getCrewId(), pageRequestDto.getDate(), pageRequestDto.getLocation(), pageable);

        // generalPostSequence로 그룹화한 후, 각 그룹을 GeneralPostSequenceResponseDto로 변환
        List<GeneralPostSequenceResponseDto> responseDtos = runJoinPosts.getContent().stream()
                .filter(post -> post.getGeneralPostSequence() != null)  // generalPostSequence가 null이 아닌 항목만 필터링
                .collect(Collectors.groupingBy(RunJoinPost::getGeneralPostSequence))  // generalPostSequence로 그룹화
                .values().stream()
                .map(GeneralPostSequenceResponseDto::new)  // 각 그룹을 GeneralPostSequenceResponseDto로 변환
                .sorted((dto1, dto2) -> Integer.compare(dto2.getGeneralPostSequence(), dto1.getGeneralPostSequence()))  // 최신순으로 정렬
                .collect(Collectors.toList());

        // 새로운 Slice로 변환하여 반환 (hasNext는 기존 Slice의 hasNext() 사용)
        Slice<GeneralPostSequenceResponseDto> resultSlice = new SliceImpl<>(responseDtos, pageable, runJoinPosts.hasNext());

        // PageResponseDto에 변환된 Slice 결과를 감싸서 반환
        return new PageResponseDto<>(resultSlice);
    }



    // TODO : 공통 함수로 만들기
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
