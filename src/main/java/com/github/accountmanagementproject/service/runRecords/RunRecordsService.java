package com.github.accountmanagementproject.service.runRecords;

import com.github.accountmanagementproject.exception.CustomBadCredentialsException;
import com.github.accountmanagementproject.exception.CustomBadRequestException;
import com.github.accountmanagementproject.exception.CustomNotFoundException;
import com.github.accountmanagementproject.repository.account.user.MyUser;
import com.github.accountmanagementproject.repository.runRecords.RunRecords;
import com.github.accountmanagementproject.repository.runRecords.RunRecordsRepository;
import com.github.accountmanagementproject.service.ExeTimer;
import com.github.accountmanagementproject.service.ScrollPaginationCollection;
import com.github.accountmanagementproject.service.mapper.runRecords.RunRecordsMapper;
import com.github.accountmanagementproject.web.dto.runRecords.RunRecordsDto;
import com.github.accountmanagementproject.web.dto.runRecords.RunRecordsRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RunRecordsService {

    private final RunRecordsRepository runRecordsRepository;

    @ExeTimer
    public ScrollPaginationCollection<RunRecordsDto> getRunRecords(Integer size, Integer cursor, MyUser user) {
        Integer lastRecordId = (cursor != null) ? cursor : runRecordsRepository.findTopByOrderByIdDesc().orElseThrow(() ->
                new CustomNotFoundException.ExceptionBuilder()
                .customMessage("기록이 없습니다.").build()).getId();

        PageRequest pageRequest = PageRequest.of(0, size + 1);

        Page<RunRecords> runRecordsPage = runRecordsRepository.findByUserAndIdLessThanOrderByIdDesc(user, lastRecordId + 1, pageRequest);

        List<RunRecords> runRecordsList = runRecordsPage.getContent();
        List<RunRecordsDto> runRecordsDtoList = runRecordsList
                .stream()
                .map(RunRecordsMapper.INSTANCE::runRecordsToRunRecordsDto)
                .toList();

        boolean lastScroll = runRecordsList.size() <= size;
        List<RunRecordsDto> currentScrollItems = lastScroll ? runRecordsDtoList : runRecordsDtoList.subList(0, size);
        RunRecordsDto nextCursor = lastScroll ? null : runRecordsDtoList.get(size);

        return ScrollPaginationCollection.of(currentScrollItems, size, lastScroll, nextCursor);
    }

    @ExeTimer
    @Transactional
    public RunRecordsDto postRunRecords(RunRecordsRequestDto runRecordsRequestDto, MyUser user) {
        try {
            RunRecords runRecords = RunRecords.builder()
                    .user(user)
                    .record(runRecordsRequestDto.getRecord())
                    .distance(runRecordsRequestDto.getDistance())
                    .progress(runRecordsRequestDto.getProgress())
                    .createdAt(LocalDateTime.now())
                    .build();

            runRecordsRepository.save(runRecords);
            
            return RunRecordsMapper.INSTANCE.runRecordsToRunRecordsDto(runRecords);
        }catch (Exception e){
            throw new CustomBadRequestException.ExceptionBuilder()
                    .customMessage(e.getMessage())
                    .build();
        }
    }

    @ExeTimer
    @Transactional
    public RunRecordsDto putRunRecords(RunRecordsRequestDto runRecordsRequestDto, Integer id, MyUser user) {
        RunRecords runRecords = runRecordsRepository.findById(id).orElseThrow(()-> new CustomNotFoundException.ExceptionBuilder()
                .customMessage("기록을 찾을 수 없습니다.")
                .build());
        
        if(!runRecords.getUser().equals(user))
            throw new CustomBadCredentialsException.ExceptionBuilder()
                    .customMessage("권한이 없습니다.")
                    .build();
        
        runRecords.setDistance(runRecordsRequestDto.getDistance());
        runRecords.setRecord(runRecordsRequestDto.getRecord());
        runRecords.setProgress(runRecordsRequestDto.getProgress());
        
        return RunRecordsMapper.INSTANCE.runRecordsToRunRecordsDto(runRecords);
    }

    @ExeTimer
    @Transactional
    public String deleteRunRecords(MyUser user, Integer id) {
        RunRecords runRecords = runRecordsRepository.findById(id).orElseThrow(()-> new CustomNotFoundException.ExceptionBuilder()
                .customMessage("기록을 찾을 수 없습니다.")
                .build());

        if(!runRecords.getUser().equals(user))
            throw new CustomBadCredentialsException.ExceptionBuilder()
                    .customMessage("권한이 없습니다.")
                    .build();

        runRecordsRepository.delete(runRecords);

        return "기록을 삭제했습니다.";
    }
}
