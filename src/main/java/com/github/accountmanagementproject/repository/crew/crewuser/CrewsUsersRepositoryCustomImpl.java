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

    @Override
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


//        List<CrewsUsers> crewsUsersList = queryFactory
//                .select(qCrewsUsers)
//                .from(qCrewsUsers)
//                .join(qCrewsUsers.crewsUsersPk.user, QMyUser.myUser).fetchJoin()
//                .join(qCrewsUsers.crewsUsersPk.crew, QCrew.crew).fetchJoin() // Crew 즉시 로딩
//                .leftJoin(qCrewsUsers.crewsUsersPk.crew.crewImages, QCrewImage.crewImage) // CrewImage 즉시 로딩
//                .where(expression)
//                .groupBy(qCrewsUsers.crewsUsersPk)
//                .fetch();

        List<CrewsUsers> crewsUsersList = queryFactory
                .selectFrom(qCrewsUsers)
                .join(qCrewsUsers.crewsUsersPk.user, QMyUser.myUser).fetchJoin()
                .join(qCrewsUsers.crewsUsersPk.crew, QCrew.crew).fetchJoin()
                .leftJoin(qCrewsUsers.crewsUsersPk.crew.crewImages, QCrewImage.crewImage).fetchJoin()
                .where(expression)
                .fetch();

        return crewsUsersList;
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
