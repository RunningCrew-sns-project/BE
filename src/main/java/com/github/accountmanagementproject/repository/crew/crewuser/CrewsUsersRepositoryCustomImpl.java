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
    private final QCrewsUsers QCREWSUSERS = QCrewsUsers.crewsUsers;

    @Override//프론트 테스트를 위한 임시
    public List<CrewJoinResponse> findSimpleCrewsUsersByUserEmail(String userEmail) {

        return queryFactory.select(Projections.constructor(CrewJoinResponse.class,
                        QCREWSUSERS.crewsUsersPk.crew.crewName,
                        QCREWSUSERS.status,
                        QCREWSUSERS.applicationDate))
                .from(QCREWSUSERS)
                .join(QCREWSUSERS.crewsUsersPk.crew)
                .where(QCREWSUSERS.crewsUsersPk.user.email.eq(userEmail))
                .fetch();
    }

    @Override
    public List<CrewsUsers> findMyCrewsByEmail(String email, Boolean isAll) {
        BooleanExpression expression = mySearchConditions(email, isAll);
        return queryFactory
                .selectFrom(QCREWSUSERS)
                .join(QCREWSUSERS.crewsUsersPk.user, QMyUser.myUser).fetchJoin()
                .join(QCREWSUSERS.crewsUsersPk.crew, QCrew.crew).fetchJoin()
                .leftJoin(QCREWSUSERS.crewsUsersPk.crew.crewImages, QCrewImage.crewImage).fetchJoin()
                .where(expression)
                .orderBy(isAll==null ? QCREWSUSERS.joinDate.desc():QCREWSUSERS.applicationDate.desc())
                .fetch();
    }

    @Override
    public List<CrewsUsers> findCrewUsersByCrewId(Long crewId, Boolean all) {
        BooleanExpression expression = mySearchConditions(crewId, all);
        return queryFactory
                .selectFrom(QCREWSUSERS)
                .join(QCREWSUSERS.crewsUsersPk.user, QMyUser.myUser).fetchJoin()
                .where(expression)
                .orderBy(all==null ? QCREWSUSERS.joinDate.desc():QCREWSUSERS.applicationDate.desc())
                .fetch();
    }


    private <T> BooleanExpression mySearchConditions(T emailOrCrewId, Boolean isAll){
        BooleanExpression expression;
        if(emailOrCrewId instanceof String){
            expression = QMyUser.myUser.email.eq((String) emailOrCrewId);
        } else if (emailOrCrewId instanceof Long) {
            expression = QCREWSUSERS.crewsUsersPk.crew.crewId.eq((Long) emailOrCrewId);
        } else {
            throw new IllegalArgumentException("emailOrCrewId must be either String or Long");
        }
        if(isAll == null){//완료된것만
            expression = expression.and(QCrewsUsers.crewsUsers.status.eq(CrewsUsersStatus.COMPLETED));
        } else if (!isAll) {//요청중인것만
            expression = expression.and(QCrewsUsers.crewsUsers.status.eq(CrewsUsersStatus.WAITING));
        }
        return expression;
    }
}
