package com.github.accountmanagementproject.service.mapper.runRecords;

import com.github.accountmanagementproject.repository.runRecords.RunRecords;
import com.github.accountmanagementproject.web.dto.runRecords.RunRecordsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RunRecordsMapper {
    RunRecordsMapper INSTANCE = Mappers.getMapper(RunRecordsMapper.class);

    @Mapping(target = "userId", source = "user.userId")
    RunRecordsDto runRecordsToRunRecordsDto(RunRecords runRecords);
}
