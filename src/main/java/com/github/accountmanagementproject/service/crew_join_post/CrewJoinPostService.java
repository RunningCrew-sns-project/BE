package com.github.accountmanagementproject.service.crew_join_post;


import com.github.accountmanagementproject.repository.account.users.MyUser;
import com.github.accountmanagementproject.repository.account.users.MyUsersJpa;
import com.github.accountmanagementproject.repository.crew.crews.CrewsJpa;
import com.github.accountmanagementproject.repository.crew_join_post.CrewJoinPost;
import com.github.accountmanagementproject.repository.crew_join_post.CrewJoinPostStatus;
import com.github.accountmanagementproject.repository.crew_join_post.repository.CrewJoinPostRepository;
import com.github.accountmanagementproject.service.crew_join_post.redis_cache.GeoRedisTemplateService;
import com.github.accountmanagementproject.web.dto.crew_join_post.CrewJoinPOstRequest;
import com.github.accountmanagementproject.web.dto.crew_join_post.CrewJoinPostDto;
import com.github.accountmanagementproject.web.dto.crew_join_post.CrewJoinPostUpdateRequest;
import com.github.accountmanagementproject.web.dto.crew_join_post.location_dto.DocumentDto;
import com.github.accountmanagementproject.web.dto.crew_join_post.location_dto.KakaoApiResponseDto;
import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class CrewJoinPostService {

    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final CrewJoinPostRepository crewJoinPostRepository;
    private final CrewsJpa crewRepository;
    private final MyUsersJpa userRepository;
    private final GeoRedisTemplateService geoRedisTemplateService;


    // (dto -> entity 변환 후 )
    //  entity 저장
    @Transactional
    public CrewJoinPost save(CrewJoinPOstRequest inputDto, Integer userId) {

        MyUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // user도 입력받아야 함, 추가 수정 예정

        DocumentDto startAddressDto = this.toDocDtoFromAddress(inputDto.getStartAddress());
        DocumentDto targetAddressDto = this.toDocDtoFromAddress(inputDto.getTargetAddress());

        if (startAddressDto == null || targetAddressDto == null) {
            throw new IllegalArgumentException("Address geocoding failed for one or both addresses.");
        }

        // 거리 계산
        double calculatedDistance = this.calculateDistance(
                startAddressDto.getLatitude(), startAddressDto.getLongitude(), targetAddressDto.getLatitude(), targetAddressDto.getLongitude());

        CrewJoinPost newPost = CrewJoinPost.builder()
                .user(user)
                .content(inputDto.getContent())
                .maxCrewNumber(inputDto.getMaxCrewNumber())
                .inputAddress(inputDto.getStartAddress())
                .inputLatitude(startAddressDto.getLatitude())
                .inputLongitude(startAddressDto.getLongitude())
                .targetAddress(inputDto.getTargetAddress())
                .targetLatitude(targetAddressDto.getLatitude())
                .targetLongitude(targetAddressDto.getLongitude())
                .distance(calculatedDistance)
                .createdAt(LocalDateTime.now())
                .status(CrewJoinPostStatus.OPEN)
                .build();
        CrewJoinPost savedPost =  crewJoinPostRepository.save(newPost);

        geoRedisTemplateService.save(CrewJoinPostDto.toDto(savedPost));
        return savedPost;
    }


    /** *********************************************************************************** */

    // dto -> entity 변환 , 공통함수
    public DocumentDto toDocDtoFromAddress(String address) {
        KakaoApiResponseDto responseDto = kakaoAddressSearchService.requestAddressSearch(address);

        if (Objects.isNull(responseDto) || CollectionUtils.isEmpty(responseDto.getDocumentList())) {
            log.error("[PharmacyRecommendationService.recommendPharmacyList fail] Input address: {}", address);
            return (DocumentDto) Collections.emptyList();
        }

        DocumentDto documentDto = responseDto.getDocumentList().get(0);
        return documentDto;
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

    /** *********************************************************************************** */

    // 목록 가져오기
    public List<CrewJoinPostDto> getAll() {
        // redis
        List<CrewJoinPostDto> crewJoinPostDtos = geoRedisTemplateService.findAll();
        if(!CollectionUtils.isEmpty(crewJoinPostDtos)) {
            return crewJoinPostDtos;
        }

        // Maria DB
        return crewJoinPostRepository.findAll().stream()
                .map(CrewJoinPostDto::toDto)
                .toList();
    }

    /**
     * 목록 가져오기
     * filter 적용
     */
    public PageResponseDto<CrewJoinPostDto> getAll(PageRequestDto pageRequestDto) {
        // Redis에서 데이터 조회
        List<CrewJoinPostDto> cachedPosts = geoRedisTemplateService.findFiltered(pageRequestDto);
        if (!CollectionUtils.isEmpty(cachedPosts)) {
            // 캐시된 데이터를 Slice 형태로 반환
            int pageSize = pageRequestDto.getSize();
            boolean hasNext = cachedPosts.size() > pageSize;
            if (hasNext) {
                cachedPosts = cachedPosts.subList(0, pageSize); // 요청된 크기만큼 잘라내기
            }
            Slice<CrewJoinPostDto> cachedSlice = new SliceImpl<>(cachedPosts, pageRequestDto.getPageable(Sort.by("createdAt").descending()), hasNext);
            return new PageResponseDto<>(cachedSlice); // PageResponseDto로 변환하여 반환
        }

        // MariaDB에서 데이터 조회
        Slice<CrewJoinPost> crewJoinPosts = crewJoinPostRepository.findByLocation(
                pageRequestDto.getLocation(), pageRequestDto.getPageable(Sort.by("createdAt").descending())
        );

        // 엔티티를 DTO로 변환
        List<CrewJoinPostDto> postDtos = crewJoinPosts.stream()
                .map(CrewJoinPostDto::toDto)
                .collect(Collectors.toList());

        // Slice 형태의 DB 결과를 PageResponseDto로 변환하여 반환
        Slice<CrewJoinPostDto> dbSlice = new SliceImpl<>(postDtos, pageRequestDto.getPageable(Sort.by("createdAt").descending()), crewJoinPosts.hasNext());
        return new PageResponseDto<>(dbSlice); // PageResponseDto로 변환하여 반환
    }



    // update
    @Transactional
    public CrewJoinPost updateCrewJoinPost(
            CrewJoinPostUpdateRequest updateRequest, Integer userId, Integer crewJoinPostId) {
        CrewJoinPost existingPost = crewJoinPostRepository.findById(crewJoinPostId).orElseThrow(
                () -> new IllegalArgumentException("CrewJoinPost not found")
        );
        MyUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        DocumentDto startAddressDto = this.toDocDtoFromAddress(updateRequest.getStartAddress());
        DocumentDto targetAddressDto = this.toDocDtoFromAddress(updateRequest.getTargetAddress());

        if (startAddressDto == null || targetAddressDto == null) {
            throw new IllegalArgumentException("Address geocoding failed for one or both addresses.");
        }

        // 거리 계산
        double calculatedDistance = this.calculateDistance(
                startAddressDto.getLatitude(), startAddressDto.getLongitude(), targetAddressDto.getLatitude(), targetAddressDto.getLongitude());

        CrewJoinPost updatedPost = CrewJoinPostUpdateRequest.toEntity(updateRequest, existingPost, user, startAddressDto, targetAddressDto, calculatedDistance);
        // Maria DB 에 저장
        CrewJoinPost newPost = crewJoinPostRepository.save(updatedPost);

        // Redis에서 기존 데이터 삭제 후, 수정된 데이터 저장
        geoRedisTemplateService.delete(Long.valueOf(crewJoinPostId)); // 기존 데이터 삭제
        geoRedisTemplateService.save(CrewJoinPostDto.toDto(newPost));

        return newPost;
    }


    // 상세보기
    @Transactional(readOnly = true)
    public CrewJoinPostDto getOneById(Integer crewJoinPostId) {
        // 1. Redis에서 데이터 조회
        CrewJoinPostDto cachedPost = geoRedisTemplateService.findById(crewJoinPostId);
        if (cachedPost != null) {
            return cachedPost; // 캐시된 데이터 반환
        }

        // 2. 데이터베이스에서 데이터 조회
        CrewJoinPost crewJoinPost = crewJoinPostRepository.findById(crewJoinPostId)
                .orElseThrow(() -> new IllegalArgumentException("CrewJoinPost not found"));

        // 3. 엔티티를 DTO로 변환
        CrewJoinPostDto postDto = CrewJoinPostDto.toDto(crewJoinPost);

//        // 4. Redis에 캐싱
//        geoRedisTemplateService.save(postDto);

        return postDto;
    }


    @Transactional
    public void deleteCrewJoinPost(Integer crewJoinPostId, Integer userId) {
        // 1. 데이터베이스에서 CrewJoinPost 삭제
        CrewJoinPost existingPost = crewJoinPostRepository.findById(crewJoinPostId)
                .orElseThrow(() -> new IllegalArgumentException("CrewJoinPost not found"));

        if(!Objects.equals(existingPost.getUser().getUserId(), userId)) {
            throw new IllegalArgumentException("CrewJoinPost id mismatch.");
        }

        // Maria DB 에서 삭제
        crewJoinPostRepository.delete(existingPost);

        // Redis 에서 데이터 삭제
        geoRedisTemplateService.delete(Long.valueOf(crewJoinPostId));

        log.info("CrewJoinPost with ID {} deleted from DB and cache", crewJoinPostId);
    }


}
