package com.github.accountmanagementproject.repository.crew.crew;

import com.github.accountmanagementproject.repository.account.user.QMyUser;
import com.github.accountmanagementproject.repository.crew.crewimage.QCrewImage;
import com.github.accountmanagementproject.web.dto.crew.CrewDetailResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CrewsRepositoryCustomImpl implements CrewsRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QCrew QCREW = QCrew.crew;

    @Override
    public Optional<CrewDetailResponse> findCrewDetailByCrewId(Long crewId) {

        List<CrewDetailResponse> response = queryFactory
                .select(Projections.constructor(CrewDetailResponse.class,
                        QCREW.crewName,
                        QCREW.crewIntroduction,
                        QCrewImage.crewImage.imageUrl,
                        QCREW.crewMaster.nickname,
                        QCREW.activityRegion,
                        QCREW.createdAt,
                        queryFactory.select(QMyUser.myUser.count().add(1))
                                .from(QMyUser.myUser)
                                .join(QMyUser.myUser.crews, QCREW)
                                .where(QCREW.crewId.eq(crewId)),
                        QCREW.maxCapacity
                ))
                .from(QCREW)
                .leftJoin(QCREW.crewImages, QCrewImage.crewImage)
                .where(QCREW.crewId.eq(crewId))
                .groupBy(QCREW.crewId)
                .fetch();



        return Optional.ofNullable(response.get(0));
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
}
