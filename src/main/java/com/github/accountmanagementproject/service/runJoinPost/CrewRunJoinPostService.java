package com.github.accountmanagementproject.service.runJoinPost;

import com.github.accountmanagementproject.exception.ResourceNotFoundException;
import com.github.accountmanagementproject.exception.UnauthorizedException;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.repository.crew.crew.CrewsRepository;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersRepository;
import com.github.accountmanagementproject.repository.runningPost.repository.RunJoinPostRepository;
import com.github.accountmanagementproject.repository.runningPost.RunJoinPost;
import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewPostSequenceResponseDto;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostCreateRequest;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class CrewRunJoinPostService {

    private final RunJoinPostRepository runJoinPostRepository;
    private final MyUsersRepository userRepository;
    private final CrewsRepository crewRepository;
    private final CrewsUsersRepository crewsUsersRepository;


    /** crew run post 시작 -----------------------------------------> */

    @Transactional
    public RunJoinPost createCrewPost(CrewRunPostCreateRequest request, MyUser user, Long crewId) {
        Crew crew = crewRepository.findByCrewMasterUserId(user.getUserId());
        if(!crew.getCrewId().equals(crewId)) {
            throw new ResourceNotFoundException("Crew does not exist");
        }

        RunJoinPost runJoinPost = CrewRunPostCreateRequest.toEntity(request, user, crew);

        // 거리 계산
        double calculatedDistance = this.calculateDistance(
                request.getInputLatitude(), request.getInputLongitude(), request.getTargetLatitude(), request.getTargetLongitude());

        // crewPostSequence 값을 설정
        Integer maxCrewPostSequence = runJoinPostRepository.findMaxCrewPostSequenceByCrewId(crew.getCrewId());

        runJoinPost.setCrewPostSequence(maxCrewPostSequence + 1);
        runJoinPost.setDistance(calculatedDistance);

        runJoinPostRepository.save(runJoinPost);
        return runJoinPost;
    }

    // 크루 게시글 상세보기
    @Transactional(readOnly = true)
    public RunJoinPost getPostByCrewPostSequence(Integer crewPostSequence) {

        RunJoinPost crewPost = runJoinPostRepository.findByCrewPostSequence(crewPostSequence)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));

        return crewPost;
    }

    // 크루 글 수정
    @Transactional
    public RunJoinPost updateCrewPostByCrewPostSequence(Integer crewPostSequence, Long crewId, Long userId, CrewRunPostUpdateRequest request) {
        RunJoinPost crewPost = runJoinPostRepository.findByCrewPostSequence(crewPostSequence)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));
//        if(!crewPost.getCrew().getCrewId().equals(crewId)) {
//            throw new ResourceNotFoundException("Crew does not exist");
//        }

        if(!crewPost.getAuthor().getUserId().equals(userId)) {
            throw new UnauthorizedException("게시글 작성자가 아닙니다. 수정 권한이 없습니다.");
        }

        // 거리 재계산
        double calculatedDistance = this.calculateDistance(
                request.getInputLatitude(), request.getInputLongitude(), request.getTargetLatitude(), request.getTargetLongitude());

        RunJoinPost updatedPost = request.updateEntity(crewPost, crewPost.getAuthor());
        updatedPost.setDistance(calculatedDistance);

        return runJoinPostRepository.save(updatedPost);
    }


    // 게시글 삭제
    @Transactional
    public void deleteCrewPostByCrewPostSequence(Integer crewPostSequence, Long userId) {
        RunJoinPost crewPost = runJoinPostRepository.findByCrewPostSequence(crewPostSequence)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));

        if(!crewPost.getAuthor().getUserId().equals(userId)) {
            throw new UnauthorizedException("게시글 작성자가 아닙니다. 삭제 권한이 없습니다.");
        }

        runJoinPostRepository.delete(crewPost);
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
    public PageResponseDto<CrewPostSequenceResponseDto> getAllCrewPosts(PageRequestDto pageRequestDto) {
        Pageable pageable = pageRequestDto.getPageable();

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
