package com.github.accountmanagementproject.repository.crew.crewuser;

import com.github.accountmanagementproject.repository.account.user.QMyUser;
import com.github.accountmanagementproject.repository.crew.crew.QCrew;
import com.github.accountmanagementproject.repository.crew.crewimage.QCrewImage;
import com.github.accountmanagementproject.web.dto.crew.CrewJoinResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CrewsUsersRepositoryCustomImpl implements CrewsUsersRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    QCrewsUsers qCrewsUsers = QCrewsUsers.crewsUsers;

    @Override//프론트 테스트를 위한 임시 메서드
    public List<CrewJoinResponse> findSimpleCrewsUsersByUserEmail(String userEmail) {

        return queryFactory.select(Projections.constructor(CrewJoinResponse.class,
                        qCrewsUsers.crewsUsersPk.crew.crewName,
                        qCrewsUsers.status,
                        qCrewsUsers.applicationDate))
                .from(qCrewsUsers)
                .join(qCrewsUsers.crewsUsersPk.crew)
                .where(qCrewsUsers.crewsUsersPk.user.email.eq(userEmail))
                .fetch();
    }

    @Override
    public List<CrewsUsers> findMyCrewsByEmail(String email, Boolean isAll) {
        BooleanExpression expression = myCrewSearchConditions(email, isAll);

        return queryFactory
                .selectFrom(qCrewsUsers)
                .join(qCrewsUsers.crewsUsersPk.user, QMyUser.myUser).fetchJoin()
                .join(qCrewsUsers.crewsUsersPk.crew, QCrew.crew).fetchJoin()
                .leftJoin(qCrewsUsers.crewsUsersPk.crew.crewImages, QCrewImage.crewImage).fetchJoin()
                .where(expression)
                .fetch();
    }

    @Override
    public List<CrewsUsers> findIMadeCrewsByEmail(String email) {

        return queryFactory.selectFrom(qCrewsUsers)
                .join(qCrewsUsers.crewsUsersPk.crew, QCrew.crew).fetchJoin()
                .join(qCrewsUsers.crewsUsersPk.user, QMyUser.myUser).fetchJoin()
                .leftJoin(qCrewsUsers.crewsUsersPk.crew.crewImages, QCrewImage.crewImage).fetchJoin()
                .where(QMyUser.myUser.email.eq(email))
                .fetch();
    }

    private BooleanExpression myCrewSearchConditions(String email, Boolean isAll){
        BooleanExpression expression = QMyUser.myUser.email.eq(email);
        if(isAll == null){//완료된것만
            expression = expression.and(QCrewsUsers.crewsUsers.status.eq(CrewsUsersStatus.COMPLETED));
        } else if (!isAll) {//요청중인것만
            expression = expression.and(QCrewsUsers.crewsUsers.status.eq(CrewsUsersStatus.WAITING));
        }
        return expression;
    }
}
