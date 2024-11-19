package com.github.accountmanagementproject.service.runJoinPost;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPostRepository;
import com.github.accountmanagementproject.repository.runningPost.crewRunGroup.CrewRunGroup;
import com.github.accountmanagementproject.repository.runningPost.crewRunGroup.CrewRunGroupRepository;
import com.github.accountmanagementproject.repository.runningPost.enums.ParticipationStatus;
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
        //참여신청한 게시물 중에
        List<TodayRunDto> todayCrewJoinPostFromCrewRunGroup = crewRunGroupRepository
                .findAllByUser(user)
                .stream()
                .filter(crewRunGroup -> crewRunGroup.getStatus().equals(ParticipationStatus.APPROVED))
                .map(CrewRunGroup::getCrewJoinPost)
                .filter(crewJoinPost -> crewJoinPost.getDate().equals(LocalDate.now()))
                .map(post-> TodayRunDto.builder()
                        .id(post.getCrewPostId())
                        .title(post.getTitle())
                        .startDate(LocalDateTime.of(post.getDate(), post.getStartTime()))
                        .isCrew(true)
                        .build())
                .toList();

        List<TodayRunDto> todayCrewJoinPostFromCrewJoinPost = crewJoinPostRepository
                .findAllByAuthor(user)
                .stream()
                .filter(crewJoinPost -> crewJoinPost.getDate().equals(LocalDate.now()))
                .map(post -> TodayRunDto.builder()
                        .id(post.getCrewPostId())
                        .title(post.getTitle())
                        .startDate(LocalDateTime.of(post.getDate(), post.getStartTime()))
                        .isCrew(true)
                        .build())
                .toList();

        List<TodayRunDto> todayGeneralJoinPostFromRunGroup = runGroupRepository
                .findAllByUser(user)
                .stream()
                .filter(runGroup -> runGroup.getStatus().equals(ParticipationStatus.APPROVED))
                .map(RunGroup::getGeneralJoinPost)
                .filter(generalJoinPost -> generalJoinPost.getDate().equals(LocalDate.now()))
                .map(post-> TodayRunDto.builder()
                        .id(post.getGeneralPostId())
                        .title(post.getTitle())
                        .startDate(LocalDateTime.of(post.getDate(), post.getStartTime()))
                        .isCrew(false)
                        .build())
                .toList();

        List<TodayRunDto> todayGeneralJoinPostFromGeneralJoinPost = generalJoinPostRepository
                .findAllByAuthor(user)
                .stream()
                .filter(generalJoinPost -> generalJoinPost.getDate().equals(LocalDate.now()))
                .map(post -> TodayRunDto.builder()
                        .id(post.getGeneralPostId())
                        .title(post.getTitle())
                        .startDate(LocalDateTime.of(post.getDate(), post.getStartTime()))
                        .isCrew(false)
                        .build())
                .toList();


        List<TodayRunDto> todayRunDtos = new ArrayList<>();

        todayRunDtos.addAll(todayCrewJoinPostFromCrewRunGroup);
        todayRunDtos.addAll(todayCrewJoinPostFromCrewJoinPost);
        todayRunDtos.addAll(todayGeneralJoinPostFromRunGroup);
        todayRunDtos.addAll(todayGeneralJoinPostFromGeneralJoinPost);

        log.info(LocalDateTime.now().toString());

        return todayRunDtos;
    }
}
