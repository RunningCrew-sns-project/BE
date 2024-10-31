package com.github.accountmanagementproject.repository.crew.crewuser;

import com.github.accountmanagementproject.web.dto.crew.CrewJoinResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
@RequiredArgsConstructor
public class CrewsUsersRepositoryCustomImpl implements CrewsUsersRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<CrewJoinResponse> findSimpleCrewsUsersByUserEmail(String userEmail) {
        QCrewsUsers qCrewsUsers = QCrewsUsers.crewsUsers;

        return queryFactory.select(Projections.constructor(CrewJoinResponse.class,
                        qCrewsUsers.crewsUsersPk.crew.crewName,
                        qCrewsUsers.status,
                        qCrewsUsers.applicationDate))
                .from(qCrewsUsers)
                .join(qCrewsUsers.crewsUsersPk.crew)
                .where(qCrewsUsers.crewsUsersPk.user.email.eq(userEmail))
                .fetch();
    }
}
