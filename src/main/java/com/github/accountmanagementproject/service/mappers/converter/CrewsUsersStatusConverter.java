package com.github.accountmanagementproject.service.mappers.converter;
import com.github.accountmanagementproject.repository.crew.crewsUsers.CrewsUsersStatus;

public class CrewsUsersStatusConverter extends MyConverter<CrewsUsersStatus>{

    public static final Class<CrewsUsersStatus> ENUM_CLASS = CrewsUsersStatus.class;
    public CrewsUsersStatusConverter() {
        super(ENUM_CLASS);
    }
}
