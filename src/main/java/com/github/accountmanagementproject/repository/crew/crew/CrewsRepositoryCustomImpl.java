package com.github.accountmanagementproject.repository.crew.crew;

import com.github.accountmanagementproject.repository.account.user.QMyUser;
import com.github.accountmanagementproject.repository.crew.crewimage.QCrewImage;
import com.github.accountmanagementproject.repository.crew.crewuser.CrewsUsersStatus;
import com.github.accountmanagementproject.repository.crew.crewuser.QCrewsUsers;
import com.github.accountmanagementproject.web.dto.crew.CrewListResponse;
import com.github.accountmanagementproject.web.dto.infinitescrolling.criteria.CursorHolder;
import com.github.accountmanagementproject.web.dto.infinitescrolling.criteria.SearchCriteria;
import com.github.accountmanagementproject.web.dto.infinitescrolling.criteria.SearchRequest;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CrewsRepositoryCustomImpl implements CrewsRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QCrew QCREW = QCrew.crew;

    @Override
    public Optional<Crew> findCrewDetailByCrewId(Long crewId) {
        Crew response = queryFactory
                .select(QCREW)
                .from(QCREW)
                .leftJoin(QCREW.crewImages, QCrewImage.crewImage).fetchJoin()
                .where(QCREW.crewId.eq(crewId))
                .fetchOne();

        return Optional.ofNullable(response);
    }

    @Override
    public List<Crew> findIMadeCrewsByEmail(String email) {

        return queryFactory.selectFrom(QCREW)
                .join(QCREW.crewMaster, QMyUser.myUser).fetchJoin()
                .leftJoin(QCREW.crewImages, QCrewImage.crewImage).fetchJoin()
                .where(QMyUser.myUser.email.eq(email))
                .orderBy(QCREW.createdAt.desc())
                .fetch();
    }

    @Override
    public boolean isCrewMaster(String masterEmail, Long crewId) {
        Long fetchOne = queryFactory.select(QCREW.crewId)
                .from(QCREW)
                .where(QCREW.crewMaster.email.eq(masterEmail), QCREW.crewId.eq(crewId))
                .fetchOne();
        return fetchOne != null;
    }

    private JPQLQuery<Long> numberOfUsersSignedUpInAMonth() {
        return JPAExpressions.select(QCrewsUsers.crewsUsers.count())
                .from(QCrewsUsers.crewsUsers)
                .where(QCrewsUsers.crewsUsers.crewsUsersPk.crew.eq(QCREW)
                        .and(QCrewsUsers.crewsUsers.joinDate.after(LocalDateTime.now().minusMonths(1))));
    }

    private JPQLQuery<Long> numberOfPostsWriteInAMonth() {
        return JPAExpressions.select(QRunJoinPost.runJoinPost.count())
                .from(QRunJoinPost.runJoinPost)
                .where(QRunJoinPost.runJoinPost.crew.eq(QCREW)
                        .and(QRunJoinPost.runJoinPost.createdAt.after(LocalDateTime.now().minusDays(7))));
    }

    private JPQLQuery<Long> completedCrewUsersCount() {
        return JPAExpressions.select(QCrewsUsers.crewsUsers.count())
                .from(QCrewsUsers.crewsUsers)
                .where(QCrewsUsers.crewsUsers.crewsUsersPk.crew.eq(QCREW)
                        .and(QCrewsUsers.crewsUsers.status.eq(CrewsUsersStatus.COMPLETED)));
    }

    @Override
    public List<CrewListResponse> findAvailableCrews(String email, SearchRequest request) {

        OrderSpecifier<?>[] specifier = setCrewListSpecifiers(request.getSearchCriteria(), request.isReverse());
        BooleanExpression expression = setCrewListExpression(email, request);
        Expression<CrewListResponse> projections = setCrewListProjections(request.getSearchCriteria());

        JPQLQuery<CrewListResponse> query = queryFactory.select(projections)
                .from(QCREW)
                .leftJoin(QCREW.crewUsers, QMyUser.myUser)
                .leftJoin(QCREW.crewImages, QCrewImage.crewImage);
        if(request.getSearchCriteria()==SearchCriteria.ACTIVITIES){
            query = query.leftJoin(QRunJoinPost.runJoinPost)
                    .on(QRunJoinPost.runJoinPost.crew.eq(QCREW));
        }
        return query
                .where(expression)
                .groupBy(QCREW)
                .orderBy(specifier)
                .limit(request.getSize() + 1)
                .fetch();

    }


    private Expression<CrewListResponse> setCrewListProjections(SearchCriteria searchCriteria) {
        return Projections.constructor(CrewListResponse.class,
                QCREW.crewId,
                QCREW.crewName,
                QCrewImage.crewImage.imageUrl,
                QCREW.crewIntroduction,
                QCREW.activityRegion,
                QCREW.createdAt,
                completedCrewUsersCount(),
                QCREW.maxCapacity,
                switch (searchCriteria) {
                    case POPULAR -> numberOfUsersSignedUpInAMonth();
                    case ACTIVITIES -> numberOfPostsWriteInAMonth();
                    default -> Expressions.constant(0L);
                });
    }

    private BooleanExpression setCrewListExpression(String email, SearchRequest request) {
        BooleanExpression expression = email.equals("anonymousUser") ? QCREW.maxCapacity.gt(QCREW.crewUsers.size())
                : QMyUser.myUser.isNull().or(QMyUser.myUser.email.ne(email)).and(QCREW.maxCapacity.gt(QCREW.crewUsers.size()));

        if (request.getCursor() != null) expression = expression.and(cursorExpressionDetails(request));
        return expression;
    }

    private BooleanExpression cursorExpressionDetails(SearchRequest request) {
        CursorHolder cursorHolder = request.getCursorHolder();
        boolean reverse = request.isReverse();

        return switch (request.getSearchCriteria()) {
            case POPULAR -> getPopularCursorCondition(cursorHolder, reverse);
            case NAME -> getNameCursorCondition(cursorHolder, reverse);
            case MEMBER -> getMemberCursorCondition(cursorHolder, reverse);
            case LATEST-> getLatestCursorCondition(cursorHolder, reverse);
            case ACTIVITIES -> getActivityCursorCondition(cursorHolder, reverse);
        };
    }

    private BooleanExpression getPopularCursorCondition(CursorHolder cursorHolder, boolean reverse) {
        JPQLQuery<Long> signedInAMonth = numberOfUsersSignedUpInAMonth();
        BooleanExpression idCondition = getIdCursorCondition(cursorHolder.getIdCursor(), reverse);
        BooleanExpression popularCondition = reverse
                ? signedInAMonth.gt(cursorHolder.getPopularOrActivitiesCursor())
                : signedInAMonth.lt(cursorHolder.getPopularOrActivitiesCursor());
        return popularCondition
                .or(signedInAMonth.eq(cursorHolder.getPopularOrActivitiesCursor()).and(idCondition));

    }

    private BooleanExpression getIdCursorCondition(Long cursorId, boolean reverse) {
        return reverse
                ? QCREW.crewId.goe(cursorId)
                : QCREW.crewId.loe(cursorId);
    }

    private BooleanExpression getNameCursorCondition(CursorHolder cursorHolder, boolean reverse) {
        BooleanExpression idCondition = getIdCursorCondition(cursorHolder.getIdCursor(), reverse);
        BooleanExpression nameCondition = reverse
                ? QCREW.crewName.lt(cursorHolder.getNameCursor())
                : QCREW.crewName.gt(cursorHolder.getNameCursor());

        return nameCondition
                .or(QCREW.crewName.eq(cursorHolder.getNameCursor()).and(idCondition));
    }
    private BooleanExpression getActivityCursorCondition(CursorHolder cursorHolder, boolean reverse) {
        JPQLQuery<Long> wroteInAMonth = numberOfPostsWriteInAMonth();
        BooleanExpression idCondition = getIdCursorCondition(cursorHolder.getIdCursor(), reverse);
        BooleanExpression wroteCondition = reverse
                ? wroteInAMonth.gt(cursorHolder.getPopularOrActivitiesCursor())
                : wroteInAMonth.lt(cursorHolder.getPopularOrActivitiesCursor());

        return wroteCondition
                .or(wroteInAMonth.eq(cursorHolder.getPopularOrActivitiesCursor()).and(idCondition));
    }

    private BooleanExpression getMemberCursorCondition(CursorHolder cursorHolder, boolean reverse) {
        BooleanExpression idCondition = getIdCursorCondition(cursorHolder.getIdCursor(), reverse);
        JPQLQuery<Long> completedUserCount = completedCrewUsersCount();
        BooleanExpression memberCondition = reverse
                ? completedUserCount.gt(cursorHolder.getMemberCursor())
                : completedUserCount.lt(cursorHolder.getMemberCursor());

        return memberCondition.
                or(completedUserCount.eq(cursorHolder.getMemberCursor()).and(idCondition));
    }

    private BooleanExpression getLatestCursorCondition(CursorHolder cursorHolder, boolean reverse) {
        BooleanExpression idCondition = getIdCursorCondition(cursorHolder.getIdCursor(), reverse);
        BooleanExpression createdAtCondition = reverse
                ? QCREW.createdAt.gt(cursorHolder.getCreatedAtCursor())
                : QCREW.createdAt.lt(cursorHolder.getCreatedAtCursor());

        return createdAtCondition
                .or(QCREW.createdAt.eq(cursorHolder.getCreatedAtCursor()).and(idCondition));
    }


    private OrderSpecifier<?>[] setCrewListSpecifiers(SearchCriteria criteria, boolean reverse) {
        OrderSpecifier<Long> subOrderBy = reverse ? QCREW.crewId.asc() : QCREW.crewId.desc();
        return switch (criteria) {
            case NAME -> new OrderSpecifier<?>[]{
                    reverse ? QCREW.crewName.desc() : QCREW.crewName.asc(),
                    subOrderBy
            };
            case MEMBER -> new OrderSpecifier<?>[]{
                    reverse ? Expressions.asNumber(completedCrewUsersCount()).asc() : Expressions.asNumber(completedCrewUsersCount()).desc(),
                    subOrderBy
            };
            case LATEST -> new OrderSpecifier<?>[]{
                    reverse ? QCREW.createdAt.asc() : QCREW.createdAt.desc(),
                    subOrderBy
            };
            case POPULAR -> new OrderSpecifier<?>[]{
                    reverse ? Expressions.asNumber(numberOfUsersSignedUpInAMonth()).asc() : Expressions.asNumber(numberOfUsersSignedUpInAMonth()).desc(),
                    subOrderBy
            };
            case ACTIVITIES -> new OrderSpecifier<?>[]{
                    reverse ? Expressions.asNumber(numberOfPostsWriteInAMonth()).asc() : Expressions.asNumber(numberOfPostsWriteInAMonth()).desc(),
                    subOrderBy
            };
        };
    }


}
