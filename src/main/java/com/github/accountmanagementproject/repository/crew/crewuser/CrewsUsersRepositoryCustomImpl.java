package com.github.accountmanagementproject.repository.crew.crewuser;

import com.github.accountmanagementproject.repository.account.user.QMyUser;
import com.github.accountmanagementproject.repository.crew.crew.QCrew;
import com.github.accountmanagementproject.repository.crew.crewimage.QCrewImage;
import com.github.accountmanagementproject.web.dto.account.crew.UserAboutCrew;
import com.github.accountmanagementproject.web.dto.crew.CrewJoinResponse;
import com.github.accountmanagementproject.web.dto.crew.MyCrewResponse;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Override
    public long countCrewUsersByCrewId(Long crewId) {
        return Optional.ofNullable(
                queryFactory
                        .select(QCREWSUSERS.count())
                        .from(QCREWSUSERS)
                        .where(QCREWSUSERS.crewsUsersPk.crew.crewId.eq(crewId)
                                .and(QCREWSUSERS.status.eq(CrewsUsersStatus.COMPLETED)))
                        .fetchOne()
        ).orElse(0L);
    }

    @Override
    public UserAboutCrew findByCrewIdAndUserEmail(Long crewId, String email) {
        return queryFactory.select(Projections.fields(UserAboutCrew.class,
                        QCREWSUSERS.status,
                        QCREWSUSERS.joinDate,
                        QCREWSUSERS.applicationDate,
                        QCREWSUSERS.withdrawalDate,
                        QCREWSUSERS.caveat,
                        ExpressionUtils.as(Expressions.constant(false), "isMaster")))
                .from(QCREWSUSERS)
                .where(QCREWSUSERS.crewsUsersPk.crew.crewId.eq(crewId)
                        .and(QCREWSUSERS.crewsUsersPk.user.email.eq(email)))
                .fetchOne();
    }

    @Override
    public void findByPkAndPlusCaveatCount(Long crewId, Long badUserId) {
       queryFactory.update(QCREWSUSERS)
                .set(QCREWSUSERS.caveat, QCREWSUSERS.caveat.add(1))
                .where(QCREWSUSERS.crewsUsersPk.crew.crewId.eq(crewId)
                        .and(QCREWSUSERS.crewsUsersPk.user.userId.eq(badUserId)))
                .execute();
    }

    @Override
    public boolean withdrawalCrew(String email, Long crewId) {
        long result = queryFactory.update(QCREWSUSERS)
                .set(QCREWSUSERS.status, CrewsUsersStatus.WITHDRAWAL)
                .set(QCREWSUSERS.withdrawalDate, LocalDateTime.now())
                .where(QCREWSUSERS.status.eq(CrewsUsersStatus.COMPLETED)
                        .and(QCREWSUSERS.crewsUsersPk.crew.crewId.eq(crewId))
                        .and(QCREWSUSERS.crewsUsersPk.user.userId.eq(
                                JPAExpressions.select(QMyUser.myUser.userId)
                                        .from(QMyUser.myUser)
                                        .where(QMyUser.myUser.email.eq(email))
                        )))
                .execute();
        return result == 1;
    }

    @Override
    public List<MyCrewResponse> findMyCrewResponseByEmail(String email, Boolean isAll) {
        mySearchConditions(email, isAll);
        return queryFactory.select(Projections.fields(MyCrewResponse.class,
                        QCREWSUSERS.crewsUsersPk.crew.crewId,
                        QCREWSUSERS.crewsUsersPk.crew.crewName,
                        QCrewImage.crewImage.imageUrl.as("crewImageUrl"),
                        QCREWSUSERS.crewsUsersPk.crew.crewIntroduction,
                        QCREWSUSERS.crewsUsersPk.crew.activityRegion,
                        QCREWSUSERS.crewsUsersPk.crew.maxCapacity,
                        QCREWSUSERS.status,
                        isAll!=null ? QCREWSUSERS.applicationDate.as("requestOrCompletionDate") : QCREWSUSERS.joinDate.as("requestOrCompletionDate"),
                        ExpressionUtils.as(
                                JPAExpressions.select(QCREWSUSERS.count())
                                        .from(QCREWSUSERS)
                                        .where(
                                                QCREWSUSERS.crewsUsersPk.crew.eq(QCrew.crew)
                                                        .and(QCREWSUSERS.status.eq(CrewsUsersStatus.COMPLETED))
                                        ), "memberCount")))
                .from(QCREWSUSERS)
                .join(QCREWSUSERS.crewsUsersPk.user, QMyUser.myUser)
                .join(QCREWSUSERS.crewsUsersPk.crew, QCrew.crew)
                .leftJoin(QCREWSUSERS.crewsUsersPk.crew.crewImages, QCrewImage.crewImage)
                .where(mySearchConditions(email, isAll))
                .groupBy(QCREWSUSERS.crewsUsersPk.crew)
                .orderBy(isAll==null ? QCREWSUSERS.joinDate.desc():QCREWSUSERS.applicationDate.desc())
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
