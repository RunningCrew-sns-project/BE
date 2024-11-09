package com.github.accountmanagementproject.repository.crew.crew;

import com.github.accountmanagementproject.repository.account.user.QMyUser;
import com.github.accountmanagementproject.repository.crew.crewimage.QCrewImage;
import com.github.accountmanagementproject.web.dto.crew.CrewListResponse;
import com.github.accountmanagementproject.web.dto.infinitescrolling.criteria.CursorHolder;
import com.github.accountmanagementproject.web.dto.infinitescrolling.criteria.SearchCriteria;
import com.github.accountmanagementproject.web.dto.infinitescrolling.criteria.SearchRequest;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

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

    @Override
    public List<CrewListResponse> findAvailableCrews(String email, SearchRequest request) {

        OrderSpecifier<?>[] specifier = setSortingCriteria( request.getSearchCriteria(), request.isReverse() );
        BooleanExpression expression = setExpression( email, request );

        return queryFactory.select(
                        Projections.constructor(CrewListResponse.class,
                                QCREW.crewId,
                                QCREW.crewName,
                                QCrewImage.crewImage.imageUrl,
                                QCREW.crewIntroduction,
                                QCREW.activityRegion,
                                QCREW.createdAt,
                                QCREW.crewUsers.size().longValue(),
                                QCREW.maxCapacity))
                .from(QCREW)
                .leftJoin(QCREW.crewUsers, QMyUser.myUser)
                .leftJoin(QCREW.crewImages, QCrewImage.crewImage)
                .where(expression)
                .groupBy(QCREW)
                .orderBy(specifier)
                .limit(request.getSize()+1)
                .fetch();
    }

    private BooleanExpression setExpression(String email, SearchRequest request) {
        BooleanExpression expression = email.equals("anonymousUser") ? QCREW.maxCapacity.gt(QCREW.crewUsers.size())
                : QMyUser.myUser.isNull().or(QMyUser.myUser.email.ne(email)).and(QCREW.maxCapacity.gt(QCREW.crewUsers.size()));

        if (request.getCursor()!=null) expression = expression.and( cursorExpressionDetails(request) );
        return expression;
    }

    private BooleanExpression cursorExpressionDetails(SearchRequest request) {
        CursorHolder cursorHolder = request.getCursorHolder();
        boolean reverse = request.isReverse();

        switch (request.getSearchCriteria()){
            case NAME -> {
                return getNameCursorCondition( cursorHolder, reverse );
            }
            case MEMBER -> {
                return getMemberCursorCondition( cursorHolder, reverse );
            }
            case LATEST -> {
                return getLatestCursorCondition( cursorHolder, reverse );
            }
        }
        throw new IllegalArgumentException("Invalid criteria");

    }
    private BooleanExpression getIdCursorCondition(CursorHolder cursorHolder, boolean reverse) {
        return reverse
                ? QCREW.crewId.goe(cursorHolder.getIdCursor())
                : QCREW.crewId.loe(cursorHolder.getIdCursor());
    }

    private BooleanExpression getNameCursorCondition(CursorHolder cursorHolder, boolean reverse) {
        BooleanExpression idCondition = getIdCursorCondition(cursorHolder, reverse);
        BooleanExpression nameCondition = reverse
                ? QCREW.crewName.lt(cursorHolder.getNameCursor())
                : QCREW.crewName.gt(cursorHolder.getNameCursor());

        return nameCondition
                .or( QCREW.crewName.eq(cursorHolder.getNameCursor()).and(idCondition) );
    }

    private BooleanExpression getMemberCursorCondition(CursorHolder cursorHolder, boolean reverse) {
        BooleanExpression idCondition = getIdCursorCondition(cursorHolder, reverse);
        BooleanExpression memberCondition = reverse
                ? QCREW.crewUsers.size().gt(cursorHolder.getMemberCursor())
                : QCREW.crewUsers.size().lt(cursorHolder.getMemberCursor());

        return memberCondition.
        or( QCREW.crewUsers.size().eq(cursorHolder.getMemberCursor()).and(idCondition) );
    }

    private BooleanExpression getLatestCursorCondition(CursorHolder cursorHolder, boolean reverse) {
        BooleanExpression idCondition = getIdCursorCondition(cursorHolder, reverse);
        BooleanExpression createdAtCondition = reverse
                ? QCREW.createdAt.gt(cursorHolder.getCreatedAtCursor())
                : QCREW.createdAt.lt(cursorHolder.getCreatedAtCursor());

        return createdAtCondition
                .or( QCREW.createdAt.eq(cursorHolder.getCreatedAtCursor()).and(idCondition) );
    }


    private OrderSpecifier<?>[] setSortingCriteria(SearchCriteria criteria, boolean reverse){
        switch (criteria){
            case NAME ->
            {
                return new OrderSpecifier<?>[]{
                        reverse ? QCREW.crewName.desc() : QCREW.crewName.asc(),
                        reverse ? QCREW.crewId.asc() : QCREW.crewId.desc()
                };
            }
            case MEMBER ->{
                return new OrderSpecifier<?>[]{
                        reverse ? QCREW.crewUsers.size().asc() : QCREW.crewUsers.size().desc(),
                        reverse ? QCREW.crewId.asc() : QCREW.crewId.desc()
                };
            }
            case LATEST -> {
                return new OrderSpecifier<?>[]{
                        reverse ? QCREW.createdAt.asc() : QCREW.createdAt.desc(),
                        reverse ? QCREW.crewId.asc() : QCREW.crewId.desc()
                };
            }
        }
        throw new IllegalArgumentException("Invalid criteria");
    }


}
