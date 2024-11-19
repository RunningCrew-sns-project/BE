package com.github.accountmanagementproject.service.runJoinPost;

import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;
import com.github.accountmanagementproject.repository.runningPost.crewRunGroup.CrewRunGroup;
import com.github.accountmanagementproject.repository.runningPost.crewRunGroup.CrewRunGroupRepository;
import com.github.accountmanagementproject.repository.runningPost.generalPost.GeneralJoinPost;
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


    @Transactional(readOnly = true)
    public List<TodayRunDto> getMyTodayRunPost(MyUser user) {
        List<TodayRunDto> todayCrewJoinPost = crewRunGroupRepository
                .findAllByUser(user)
                .stream()
                .map(CrewRunGroup::getCrewJoinPost)
                .filter(crewJoinPost -> crewJoinPost.getDate().equals(LocalDate.now()))
                .map(post-> TodayRunDto.builder()
                        .id(post.getCrewPostId())
                        .title(post.getTitle())
                        .startDate(LocalDateTime.of(post.getDate(), post.getStartTime()))
                        .isCrew(true)
                        .build())
                .toList();

        List<TodayRunDto> todayGeneralJoinPost = runGroupRepository
                .findAllByUser(user)
                .stream()
                .map(RunGroup::getGeneralJoinPost)
                .filter(generalJoinPost -> generalJoinPost.getDate().equals(LocalDate.now()))
                .map(post-> TodayRunDto.builder()
                        .id(post.getGeneralPostId())
                        .title(post.getTitle())
                        .startDate(LocalDateTime.of(post.getDate(), post.getStartTime()))
                        .isCrew(false)
                        .build())
                .toList();
        log.info(String.valueOf(LocalDate.now()));

        List<TodayRunDto> todayRunDtos = new ArrayList<>();
        todayRunDtos.addAll(todayCrewJoinPost);
        todayRunDtos.addAll(todayGeneralJoinPost);

        return todayRunDtos;
    }
}
