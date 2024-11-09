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
        //기본 조건 이미 가입한 크루와 가입 제한인원을 넘은 크루는 제외
        BooleanExpression expression = email.equals("annonymous") ? QCREW.maxCapacity.gt(QCREW.crewUsers.size())
                : QMyUser.myUser.isNull().or(QMyUser.myUser.email.ne(email));

        OrderSpecifier<?> specifier = setSortingCriteria(request.getSearchCriteria(), request.isReverse());

        if (request.getCursor()!=null) expression = expression.and( cursorExpression(request) );

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

    private BooleanExpression cursorExpression(SearchRequest request) {
        CursorHolder cursorHolder = request.getCursorHolder();
        boolean reverse = request.isReverse();

        switch (request.getSearchCriteria()){
            case NAME -> {
                return reverse ? QCREW.crewName.gt(cursorHolder.getNameCursor())
                        : QCREW.crewName.lt(cursorHolder.getNameCursor());
            }
            case MEMBER -> {
                return reverse ? QCREW.crewUsers.size().gt(cursorHolder.getMemberCursor()) :
                        QCREW.crewUsers.size().lt(cursorHolder.getMemberCursor());
            }
            case LATEST -> {
                return reverse ? QCREW.createdAt.goe(cursorHolder.getCreatedAtCursor())
                            : QCREW.createdAt.loe(cursorHolder.getCreatedAtCursor());
            }
        }
        throw new IllegalArgumentException("Invalid criteria");

    }

    private OrderSpecifier<?> setSortingCriteria(SearchCriteria criteria, boolean reverse){
        switch (criteria){
            case NAME ->
            {
                return reverse ? QCREW.crewName.asc() : QCREW.crewName.desc();
            }
            case MEMBER ->{
                return reverse ? QCREW.crewUsers.size().asc() : QCREW.crewUsers.size().desc();
            }
            case LATEST -> {
                return reverse ? QCREW.createdAt.asc() : QCREW.createdAt.desc();
            }
        }
        throw new IllegalArgumentException("Invalid criteria");
    }


}
