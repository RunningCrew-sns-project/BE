package com.github.accountmanagementproject.repository.crew.crew;

import com.github.accountmanagementproject.repository.account.user.QMyUser;
import com.github.accountmanagementproject.repository.crew.crewimage.QCrewImage;
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
    public List<Crew> findFilteredCrews(String location, Integer cursor, int size) {
        QCrew crew = QCrew.crew;
        QCrewImage crewImage = QCrewImage.crewImage;

        // 커서 조건: cursor가 null이 아니면 해당 ID보다 작은 ID만 가져오기
        BooleanExpression cursorCondition = (cursor != null) ? crew.crewId.lt(cursor) : null;

        // 지역 필터링 조건 설정
        BooleanExpression locationCondition = (location != null && !location.trim().isEmpty() && !location.equals("전체"))
                ? crew.activityRegion.eq(location)
                : null;

        List<Crew> crews = queryFactory.selectFrom(crew)
                .where(cursorCondition, locationCondition)
                .leftJoin(crew.crewImages, crewImage).fetchJoin()
                .orderBy(crew.createdAt.desc())
                .limit(size + 1)  // 요청한 size보다 1개 더 가져와서 다음 페이지 확인
                .fetch();

        return crews;
    }


}