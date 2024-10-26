package com.github.accountmanagementproject.repository.crew.crewsUsers;

import com.github.accountmanagementproject.service.mappers.converter.CrewsUsersStatusConverter;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "crews_users")
@Getter
public class CrewsUsers {
    @EmbeddedId
    private CrewsUsersPk crewsUsersPk;
    @Convert(converter = CrewsUsersStatusConverter.class)
    private CrewsUsersStatus status;
    @Column(name = "join_date")
    private String joinDate;
}
