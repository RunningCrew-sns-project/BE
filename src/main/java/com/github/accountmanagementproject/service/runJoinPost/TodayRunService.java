package com.github.accountmanagementproject.service.runJoinPost;

import com.github.accountmanagementproject.exception.CustomNotFoundException;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPostRepository;
import com.github.accountmanagementproject.repository.runningPost.crewRunGroup.CrewRunGroup;
import com.github.accountmanagementproject.repository.runningPost.crewRunGroup.CrewRunGroupRepository;
import com.github.accountmanagementproject.repository.runningPost.enums.CrewRunJoinPostStatus;
import com.github.accountmanagementproject.repository.runningPost.enums.GeneralRunJoinPostStatus;
import com.github.accountmanagementproject.repository.runningPost.enums.ParticipationStatus;
import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPost;
import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPostRepository;
import com.github.accountmanagementproject.repository.runningPost.runGroup.RunGroup;
import com.github.accountmanagementproject.repository.runningPost.runGroup.RunGroupRepository;
import com.github.accountmanagementproject.web.dto.runJoinPost.todayRun.TodayRunDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodayRunService {
    private static final Logger log = LoggerFactory.getLogger(TodayRunService.class);
    private final CrewRunGroupRepository crewRunGroupRepository;
    private final RunGroupRepository runGroupRepository;
    private final CrewJoinPostRepository crewJoinPostRepository;
    private final GeneralJoinPostRepository generalJoinPostRepository;


    @Transactional(readOnly = true)
    public List<TodayRunDto> getMyTodayRunPost(MyUser user) {
        //종료 상태 아닌 게시글만 필터링

        //크루 달리기 참여자인 경우
        List<TodayRunDto> todayCrewJoinPostFromCrewRunGroup = crewRunGroupRepository
                .findAllByUser(user)
                .stream()
                .filter(crewRunGroup -> crewRunGroup.getStatus().equals(ParticipationStatus.APPROVED))
                .map(CrewRunGroup::getCrewJoinPost)
                .filter(crewJoinPost -> crewJoinPost.getDate().equals(LocalDate.now()))
//                .filter(crewJoinPost -> !crewJoinPost.getStatus().equals(CrewRunJoinPostStatus.END))
                .map(this::mappingCrewJoinPost)
                .toList();

        //크루 달리기 작성자인 경우
        List<TodayRunDto> todayCrewJoinPostFromCrewJoinPost = crewJoinPostRepository
                .findAllByAuthor(user)
                .stream()
                .filter(crewJoinPost -> crewJoinPost.getDate().equals(LocalDate.now()))
//                .filter(crewJoinPost -> !crewJoinPost.getStatus().equals(CrewRunJoinPostStatus.END))
                .map(this::mappingCrewJoinPost)
                .toList();

        //일반 달리기 참여자인 경우
        List<TodayRunDto> todayGeneralJoinPostFromRunGroup = runGroupRepository
                .findAllByUser(user)
                .stream()
                .filter(runGroup -> runGroup.getStatus().equals(ParticipationStatus.APPROVED))
                .map(RunGroup::getGeneralJoinPost)
                .filter(generalJoinPost -> generalJoinPost.getDate().equals(LocalDate.now()))
//                .filter(generalJoinPost -> !generalJoinPost.getStatus().equals(GeneralRunJoinPostStatus.END))
                .map(this::mappingGeneralJoinPost)
                .toList();

        //일반 달리기 작성자인 경우
        List<TodayRunDto> todayGeneralJoinPostFromGeneralJoinPost = generalJoinPostRepository
                .findAllByAuthor(user)
                .stream()
                .filter(generalJoinPost -> generalJoinPost.getDate().equals(LocalDate.now()))
//                .filter(generalJoinPost -> !generalJoinPost.getStatus().equals(GeneralRunJoinPostStatus.END))
                .map(this::mappingGeneralJoinPost)
                .toList();


        List<TodayRunDto> todayRunDtos = new ArrayList<>();

        todayRunDtos.addAll(todayCrewJoinPostFromCrewRunGroup);
        todayRunDtos.addAll(todayCrewJoinPostFromCrewJoinPost);
        todayRunDtos.addAll(todayGeneralJoinPostFromRunGroup);
        todayRunDtos.addAll(todayGeneralJoinPostFromGeneralJoinPost);

        return todayRunDtos;
    }

    private TodayRunDto mappingCrewJoinPost(CrewJoinPost post){
       return TodayRunDto.builder()
                .id(post.getCrewPostId())
                .title(post.getTitle())
                .startDate(LocalDateTime.of(post.getDate(), post.getStartTime()))
                .isCrew(true)
                .build();
    }

    private TodayRunDto mappingGeneralJoinPost(GeneralJoinPost post){
        return TodayRunDto.builder()
                .id(post.getGeneralPostId())
                .title(post.getTitle())
                .startDate(LocalDateTime.of(post.getDate(), post.getStartTime()))
                .isCrew(false)
                .build();
    }

    @Transactional
    public Object completeTodayGeneralRun(MyUser user, Long runPostId) {
        GeneralJoinPost generalJoinPost = generalJoinPostRepository.findById(runPostId).orElseThrow(()->new CustomNotFoundException.ExceptionBuilder()
                .customMessage("게시글을 찾을 수 없습니다.")
                .request(runPostId)
                .build());

        if(generalJoinPost.getAuthor().equals(user)){ //user가 작성자라면
            if(runGroupRepository.findAllByGeneralJoinPost(generalJoinPost).isEmpty()) // 참여한 모든 유저가 종료 했다면
                generalJoinPost.setStatus(GeneralRunJoinPostStatus.END); // 종료 상태로 전환
        }else { //작성자가 아니고 참여자라면
            RunGroup runGroup = runGroupRepository.findByGeneralJoinPostAndUser(generalJoinPost, user);
            runGroupRepository.delete(runGroup);
        }

        return "일반 달리기가 종료되었습니다.";
    }

    @Transactional
    public Object completeTodayCrewRun(MyUser user, Long runPostId) {
        CrewJoinPost crewJoinPost = crewJoinPostRepository.findById(runPostId).orElseThrow(()->new CustomNotFoundException.ExceptionBuilder()
                .customMessage("게시글을 찾을 수 없습니다.")
                .request(runPostId)
                .build());

        if(crewJoinPost.getAuthor().equals(user)){ //user가 작성자라면
            if(crewRunGroupRepository.findAllByCrewJoinPost(crewJoinPost).isEmpty())
                crewJoinPost.setStatus(CrewRunJoinPostStatus.END);
        }else {
            CrewRunGroup crewRunGroup = crewRunGroupRepository.findByCrewJoinPostAndUser(crewJoinPost, user);
            crewRunGroupRepository.delete(crewRunGroup);
        }

        return "크루 달리기가 종료되었습니다.";
    }
}
