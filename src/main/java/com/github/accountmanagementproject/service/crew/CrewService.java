package com.github.accountmanagementproject.service.crew;

import com.github.accountmanagementproject.alarm.service.NotificationService;
import com.github.accountmanagementproject.config.security.AccountConfig;
import com.github.accountmanagementproject.exception.*;
import com.github.accountmanagementproject.exception.enums.ErrorCode;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.account.user.MyUsersRepository;
import com.github.accountmanagementproject.repository.crew.crew.Crew;
import com.github.accountmanagementproject.repository.crew.crew.CrewsRepository;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsers;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersPk;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersRepository;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersStatus;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPostRepository;
import com.github.accountmanagementproject.repository.runningPost.crewRunGroup.CrewRunGroupRepository;
import com.github.accountmanagementproject.service.ScrollPaginationCollection;
import com.github.accountmanagementproject.service.mapper.crew.CrewMapper;
import com.github.accountmanagementproject.web.dto.account.crew.UserAboutCrew;
import com.github.accountmanagementproject.web.dto.crew.*;
import com.github.accountmanagementproject.web.dto.infinitescrolling.InfiniteScrollingCollection;
import com.github.accountmanagementproject.web.dto.infinitescrolling.criteria.SearchCriteria;
import com.github.accountmanagementproject.web.dto.infinitescrolling.criteria.SearchRequest;
import com.github.accountmanagementproject.web.dto.pagination.PageRequestDto;
import com.github.accountmanagementproject.web.dto.pagination.PageResponseDto;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.crew.CrewRunPostResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrewService {
    private final AccountConfig accountConfig;
    private final CrewsRepository crewsRepository;
    private final CrewsUsersRepository crewsUsersRepository;
    private final CrewRunGroupRepository crewRunGroupRepository;
    private final NotificationService notificationService;
    private final MyUsersRepository myUsersRepository;
    private final CrewJoinPostRepository crewJoinPostRepository;

    @Transactional(readOnly = true)
    public InfiniteScrollingCollection<CrewListResponse, SearchCriteria> getAvailableCrewLists(String email, SearchRequest request) {
        if (request.getCursor() != null) request.makeCursorHolder();

        List<CrewListResponse> crewList = crewsRepository.findAvailableCrews(email, request);
        if (!crewList.isEmpty())
            crewListValidation(crewList.get(0), request);

        return InfiniteScrollingCollection.of(crewList, request.getSize(), request.getSearchCriteria());
    }

    private void crewListValidation(CrewListResponse firstCrew, SearchRequest request) {
        if (request.getCursor() == null) return;
        if (!firstCrew.valueValidity(request))
            throw new CustomBindException.ExceptionBuilder()
                    .systemMessage("유효성 검사 실패")
                    .customMessage("커서의 값과, 커서 아이디가 이 전 응답과 일치하지 않습니다.")
                    .request(request)
                    .build();
    }

    @Transactional
    public void crewCreation(@Valid CrewCreationRequest request, String email) {
        MyUser crewMaster = accountConfig.findMyUser(email);
        Crew newCrew = CrewMapper.INSTANCE.crewCreationRequestToCrew(request, crewMaster);
        crewsRepository.save(newCrew);
    }

    private Crew findsCrewById(Long crewId) {
        return crewsRepository.findById(crewId).orElseThrow(() -> new CustomNotFoundException.ExceptionBuilder()
                .customMessage("해당 크루를 찾을 수 없습니다.").request(crewId).build());
    }

    @Transactional
    public CrewJoinResponse joinTheCrew(String email, Long crewId) {
        MyUser user = accountConfig.findMyUser(email);
        Crew crew = findsCrewById(crewId);
        long crewUserCount = crewsUsersRepository.countCrewUsersByCrewId(crewId);

        if(crew.getMaxCapacity() <= crewUserCount)
            throw new CustomBadRequestException.ExceptionBuilder()
                    .customMessage("크루 인원이 가득 찼습니다.")
                    .request(crewUserCount)
                    .build();

        CrewsUsersPk crewsUsersPk = new CrewsUsersPk(crew, user);

        CrewsUsers joinCrewsUsers = validateAndJoinCrew(crewsUsersPk);
        notificationService.sendRequestNotification(crew.getCrewMaster().getUserId(), crewId, user.getUserId(), "크루 가입 요청이 들어왔습니다.");  // 알림
        return CrewMapper.INSTANCE.crewsUsersToCrewJoinResponse(joinCrewsUsers);
    }


    private CrewsUsers validateAndJoinCrew(CrewsUsersPk crewsUsersPk) {
        if (crewsUsersPk.getCrew().getCrewMaster().equals(crewsUsersPk.getUser()))
            throw new DuplicateKeyException.ExceptionBuilder()
                    .systemMessage("유효성 검사 실패").customMessage("자기가 만든 크루에 가입할 수 없습니다.").request(crewsUsersPk.getCrew().getCrewName()).build();

        CrewsUsers crewsUsers = crewsUsersRepository.findById(crewsUsersPk)
                .orElseGet(() -> new CrewsUsers(crewsUsersPk));
        checkJoinRequestStatus(crewsUsers);

        return crewsUsersRepository.save(crewsUsers.requestToJoin());
    }

    private void checkJoinRequestStatus(CrewsUsers crewsUsers) {
        boolean isNewRequest = crewsUsers.getStatus() == null;

        if (!isNewRequest && crewsUsers.duplicateRequest()) {
            throw new DuplicateKeyException.ExceptionBuilder()
                    .systemMessage("유효성 검사 실패")
                    .customMessage("이미 가입했거나 가입 요청 중인 크루입니다.")
                    .request(crewsUsers.getStatus())
                    .build();
        } else if (!isNewRequest && LocalDate.now().isBefore(crewsUsers.getReleaseDay())) {
            throw new CustomBindException.ExceptionBuilder()
                    .systemMessage("유효성 검사 실패")
                    .customMessage("탈퇴한 또는, 강제 퇴장이나 가입 거절 당하고 재가입 조건을 충족 못한 크루 입니다.")
                    .request(Map.of("status", crewsUsers.getStatus(), "releaseDay", crewsUsers.getReleaseDay()))
                    .build();
        }
    }


    //프론트 테스트를 위한 가입요청내역 반환
    @Transactional(readOnly = true)
    public List<CrewJoinResponse> requestTest(String email) {
        return crewsUsersRepository.findSimpleCrewsUsersByUserEmail(email);
    }

    @Transactional(readOnly = true)
    public CrewDetailResponse getCrewDetail(Long crewId) {
        Crew crew = findsCrewById(crewId);
        long crewMemberCount = crewsUsersRepository.countCrewUsersByCrewId(crewId);
        CrewDetailResponse response = CrewMapper.INSTANCE.crewToCrewDetailResponse(crew);
        response.setMemberCount(crewMemberCount + 1);
        return response;
    }

    @Transactional(readOnly = true)
    public List<CrewUserResponse> getCrewUsers(String masterEmail, Long crewId, Boolean all) {
        isCrewMaster(masterEmail, crewId);

        List<CrewsUsers> crewsUsers = crewsUsersRepository.findCrewUsersByCrewId(crewId, all);

        return crewsUsers.stream().map(CrewMapper.INSTANCE::crewsUsersToCrewUserResponse).toList();
    }

    private void isCrewMaster(String masterEmail, Long crewId) {
        boolean isCrewMaster = crewsRepository.isCrewMaster(masterEmail, crewId);
        if (!isCrewMaster)
            throw new CustomBadCredentialsException.ExceptionBuilder()
                    .request(masterEmail)
                    .customMessage("크루 마스터가 아닙니다").build();
    }


    //크루원 퇴장시키기
    @Transactional
    public String sendOutCrew(String crewMasterEmail, Long crewId, Long outUserId) {
//        //내보낼 유저
//        MyUser outCrewUser = myUsersRepository.findById(outCrewId).orElseThrow(()->new CustomNotFoundException.ExceptionBuilder()
//                .customMessage("해당 유저를 찾을 수 없습니다.").request(outCrewId).build());
//        //가입한 크루
//        Crew crew = crewsRepository.findById(crewId).orElseThrow(()->new CustomNotFoundException.ExceptionBuilder()
//                .customMessage("해당 크루를 찾을 수 없습니다.").request(crewId).build());
//
        //크루마스터인지 확인
        if (!crewsRepository.isCrewMaster(crewMasterEmail, crewId)) {
            throw new CustomBadCredentialsException.ExceptionBuilder()
                    .customMessage("크루의 마스터가 아닙니다.")
                    .build();
        }
//        //내보낼 멤버가 크루의 멤버인지 확인
//        if(!crew.getCrewUsers().contains(outCrewUser)){
//            throw new CustomNotFoundException.ExceptionBuilder()
//                    .customMessage("크루의 멤버가 아닙니다.")
//                    .build();
//        }
        //db콜 없이 pk로 객체생성
        Crew myCrew = new Crew();
        myCrew.setCrewId(crewId);
        MyUser outCrewUser = new MyUser();
        outCrewUser.setUserId(outUserId);

        //Crew와 MyUser 객체로 CrewsUsers를 찾기 위한 PK 생성
        CrewsUsersPk crewsUsersPk = new CrewsUsersPk(myCrew, outCrewUser);

        //PK로 CrewsUsers 검색 pk로 검색할수있는 기본 메서드가있어서 그거 사용했습니다!
        CrewsUsers crewsUser = crewsUsersRepository.findById(crewsUsersPk)
                .orElseThrow(() -> new CustomNotFoundException.ExceptionBuilder()
                        .customMessage("해당 크루의 멤버를 찾을 수 없습니다.").request(crewsUsersPk).build());

        //CrewsUsers의 상태 변경
        crewsUser.setStatus(CrewsUsersStatus.FORCED_EXIT); //강제 퇴장 상태로 변경
        //강퇴 날짜 지정
        crewsUser.setWithdrawalDate(LocalDateTime.now());

        //객체 저장 - 트랜잭셔널 적용되어있구 원래있던 객체 불러와서 수정한거라 save 안쓰셔도 괜찮습니다
//        crewsUsersRepository.save(crewsUser);
        Long masterUserId = myUsersRepository.findByEmail(crewMasterEmail).get().getUserId();
        notificationService.sendKickNotification(outUserId, crewId, masterUserId,  "모임에서 강퇴되었습니다.");  // 알림
        return "crewUser : " + crewsUser.getCrewsUsersPk().getUser().getNickname() + " 을/를 성공적으로 퇴장시켰습니다.";
    }

    //가입요청을 확인하여 승인, 거절 로직
    @Transactional
    public String approveOrReject(String email, Long crewId, Long requestCrewUserId, Boolean approveOrReject) {
        //크루 마스터인지 화인
        if (!crewsRepository.isCrewMaster(email, crewId)) {
            throw new CustomBadCredentialsException.ExceptionBuilder()
                    .customMessage("크루의 마스터가 아닙니다.")
                    .build();
        }

        Crew myCrew = new Crew();
        myCrew.setCrewId(crewId);

        MyUser requestCrewUser = new MyUser();
        requestCrewUser.setUserId(requestCrewUserId);

        CrewsUsersPk crewsUsersPk = new CrewsUsersPk(myCrew, requestCrewUser);

        CrewsUsers crewsUser = crewsUsersRepository.findById(crewsUsersPk)
                .orElseThrow(() -> new CustomNotFoundException.ExceptionBuilder()
                        .customMessage("해당 크루의 멤버를 찾을 수 없습니다.").request(crewsUsersPk).build());

        Long masterUserId = myUsersRepository.findByEmail(email).get().getUserId();
        //유저의 상태가 '가입 대기' 상태이고, 승인 요청이라면
        if (crewsUser.getStatus() == CrewsUsersStatus.WAITING && approveOrReject) {
            //'가입 완료' 상태로 바꾸고
            crewsUser.setStatus(CrewsUsersStatus.COMPLETED);
            crewsUser.setJoinDate(LocalDateTime.now());
            notificationService.sendApproveNotification(requestCrewUserId, crewId, masterUserId, "크루 가입 요청이 승인되었습니다."); // 알림
        } else if (crewsUser.getStatus() == CrewsUsersStatus.COMPLETED) {
            return "이미 가입된 유저입니다";
        } else {
            //가입 거절 시 '가입 거절' 로 상태 바꾸고
            crewsUser.setStatus(CrewsUsersStatus.REJECTED);
            crewsUser.setWithdrawalDate(LocalDateTime.now());
            notificationService.sendRejectNotification(requestCrewUserId, crewId, masterUserId, "크루 가입 요청이 거절되었습니다.");  // 알림
        }

        return "요청 유저 : " + requestCrewUser.getNickname() + "의 요청을 " + (approveOrReject ? "승인" : "거절") + "했습니다.";
    }


    // 크루 Info + 크루 달리기 게시물 목록
    @Transactional(readOnly = true)
    public PageResponseDto<CrewDetailWithPostsResponse> getCrewDetailsWithPosts(String email, Long crewId, PageRequestDto pageRequestDto) {
        MyUser user = accountConfig.findMyUser(email);
//        MyUser user = myUsersRepository.findByEmail(email)   //  TODO: 삭제 예정, 현재 로직에 맞춰 Not Found 가 아닌 것으로 대체함.
//                .orElseThrow(() -> new SimpleRunAppException(ErrorCode.UNAUTHORIZED_CREW_VIEW));

        // 크루 정보 조회 및 권한 확인
        Crew crew = crewsRepository.findByIdWithImages(crewId)
                .orElseThrow(() -> new CustomNotFoundException.ExceptionBuilder().customMessage("크루를 찾을 수 없습니다.").build());
        if (!isAuthorizedUser(crew, crewId, user)) {
            throw new SimpleRunAppException(ErrorCode.UNAUTHORIZED_POST_VIEW, "Unauthorized access to the crew post.");
        }

        // 크루 기본 정보 DTO로 변환
        CrewDetailResponse crewResponse = getCrewDetail(crewId);

        int size = pageRequestDto.getSize() > 0 ? pageRequestDto.getSize() : 20;
        // 게시물 목록 조회 및 페이징 처리
        List<CrewJoinPost> crewJoinPosts = crewJoinPostRepository.findFilteredCrewPosts(
                crewId,
                pageRequestDto.getDate(),
                pageRequestDto.getLocation(),
                pageRequestDto.getCursor(),
                size + 1,
                pageRequestDto.getSortType()
        );

        ScrollPaginationCollection<CrewJoinPost> scrollPagination =
                ScrollPaginationCollection.of(
                        crewJoinPosts.subList(0, Math.min(crewJoinPosts.size(), size)),
                        size,
                        crewJoinPosts.size() <= size,
                        crewJoinPosts.size() > size ? crewJoinPosts.get(size) : null
                );

        // 다음 페이지 여부 판단
        boolean hasNext = crewJoinPosts.size() > pageRequestDto.getSize();
        if (hasNext) {
            crewJoinPosts.remove(crewJoinPosts.size() - 1); // 다음 페이지 확인용으로 가져온 항목 제거
        }

        // 게시물 DTO 변환
        List<CrewRunPostResponse> postResponses = crewJoinPosts.stream()
                .map(post -> {
                    CrewRunPostResponse response = CrewRunPostResponseMapper.toDto(post, crew);
                    response.setPeople(crewRunGroupRepository.countParticipantsByPostId(post.getCrewPostId()));
                    return response;
                }).toList();

        // 다음 커서 및 마지막 페이지 여부 설정
//        boolean hasNext = postResponses.size() > pageRequestDto.getSize();
//        Integer nextCursor = hasNext ? postResponses.get(pageRequestDto.getSize() - 1).getRunId().intValue() : null;

        // 결과 응답 설정
        CrewDetailWithPostsResponse response = new CrewDetailWithPostsResponse(crewResponse, postResponses);

        // nextCursor는 다음 객체의 ID
        Integer nextCursor = null;
        if (!scrollPagination.isLastScroll()) {
            CrewJoinPost nextPost = scrollPagination.getNextCursor();
            nextCursor = Math.toIntExact(nextPost.getCrewPostId());
        }
        return new PageResponseDto<>(List.of(response), pageRequestDto.getSize(), !hasNext, nextCursor);
    }

    public boolean isAuthorizedUser(Crew crew, Long crewId, MyUser user) {
        boolean isCrewMaster = crew.getCrewMaster().getUserId().equals(user.getUserId()); // 1. 크루 마스터 확인
        boolean isCrewMember = crewsUsersRepository.existsByCrewIdAndUserIdAndStatus(  // 2. 승인된 멤버 확인
                crewId, user.getUserId(), CrewsUsersStatus.COMPLETED);
        return isCrewMaster || isCrewMember;   // 3. 둘 중 하나라도 true 이면 접근 가능
    }


    public List<CrewUserParent> getSimplyCrewUsers(Long crewId) {
        List<CrewsUsers> crewsUsers = crewsUsersRepository.findCrewUsersByCrewId(crewId, null);
        return CrewMapper.INSTANCE.crewsUsersToCrewUserParent(crewsUsers);
    }

    public UserAboutCrew userAboutCrew(String email, Long crewId) {
        UserAboutCrew masterResponse = crewsRepository.findByIdAndCrewMasterEmail(crewId, email);
        return masterResponse != null ?
                masterResponse :
                crewsUsersRepository.findByCrewIdAndUserEmail(crewId, email);

    }
    private CrewsUsersPk makeCrewsUsersPk(Long crewId, Long userId) {
        Crew crew = new Crew();
        crew.setCrewId(crewId);
        MyUser user = new MyUser();
        user.setUserId(userId);
        return new CrewsUsersPk(crew, user);
    }
    private CrewsUsers findCrewsUsersAndValidation(CrewsUsersPk pk){
        CrewsUsers badUser = crewsUsersRepository.findById(pk)
                .orElseThrow(() -> new CustomNotFoundException.ExceptionBuilder()
                        .customMessage("해당 크루의 멤버를 찾을 수 없습니다.").request(pk.getCrew().getCrewId()+" "+pk.getUser().getUserId()).build());
        if(badUser.getStatus()!=CrewsUsersStatus.COMPLETED)
            throw new CustomBadRequestException.ExceptionBuilder()
                    .customMessage("가입 완료 상태의 멤버가 아닙니다.").request(badUser.getStatus()).build();
        return badUser;
    }


    @Transactional
    public Map<String,Object> giveAUserAYellowCard(String masterEmail, Long crewId, Long badUserId) {
        isCrewMaster(masterEmail, crewId);
        CrewsUsersPk pk = makeCrewsUsersPk(crewId, badUserId);
        CrewsUsers badUser = findCrewsUsersAndValidation(pk);

        if(badUser.getCaveat() >= 2){
            badUser.setCaveat(badUser.getCaveat()+1);
            badUser.setStatus(CrewsUsersStatus.FORCED_EXIT);
            badUser.setWithdrawalDate(LocalDateTime.now());
            return Map.of("message", "누적 경고 3회로 유저가 강퇴되었습니다.", "status", badUser.getStatus(),
                    "releaseDay", badUser.getReleaseDay(), "caveat", badUser.getCaveat());
        }else {
            badUser.setCaveat(badUser.getCaveat()+1);
            return Map.of("message", "경고가 부여되었습니다.", "caveat", badUser.getCaveat());
        }
    }

    @Transactional
    public void withdrawalCrew(String email, Long crewId) {
        boolean result = crewsUsersRepository.withdrawalCrew(email, crewId);
        if(!result)
            throw new CustomNotFoundException.ExceptionBuilder()
                    .customMessage("해당 크루의 가입 완료상태인 유저가 아니거나 존재하지 않는 크루")
                    .request(Map.of("email", email, "crewId", crewId))
                    .build();
    }

    public List<CrewAndUserResponse> getAllMyCrewAWaitingUsers(String email) {
        return crewsUsersRepository.myCrewPendingUsers(email);
    }

    @Transactional
    public void transferTheCrewMaster(String oldMasterEmail, Long crewId, Long newMasterId) {
        boolean isNewMasterUserSet = crewsUsersRepository.checkCrewUserAndDelete(crewId, newMasterId);
        if(!isNewMasterUserSet) throw new CustomNotFoundException.ExceptionBuilder()
                .customMessage("해당 크루의 가입 완료상태인 유저가 아닙니다.")
                .request(Map.of("crewId", crewId, "newMasterId", newMasterId))
                .build();

        boolean isTransferTheCrewMaster = crewsRepository.replaceCrewMaster(crewId, oldMasterEmail, newMasterId);
        if(!isTransferTheCrewMaster) throw new CustomNotFoundException.ExceptionBuilder()
                .customMessage("해당 크루의 마스터가 아니거나, 존재하지 않는 크루 입니다.")
                .request(Map.of("crewId", crewId, "oldMasterEmail", oldMasterEmail))
                .build();
    }

    public void deleteCrew(String email, Long crewId) {

    }
}