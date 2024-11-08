package com.github.accountmanagementproject.repository.crew.crew;

import com.github.accountmanagementproject.repository.account.user.QMyUser;
import com.github.accountmanagementproject.repository.crew.crewimage.QCrewImage;
import com.github.accountmanagementproject.repository.crew.crewuser.QCrewsUsers;
import com.github.accountmanagementproject.web.dto.pagination.SearchCriteria;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

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
    public List<Crew> findAvailableCrews(String email, Pageable pageable, SearchCriteria criteria) {

        QCrewsUsers qCrewsUsers = QCrewsUsers.crewsUsers;
        BooleanExpression expression = qCrewsUsers.crewsUsersPk.user.email.ne(email);
        pageable.getSort();
        pageable.getPageNumber();
        pageable.getPageSize();
//        OrderSpecifier<?> orderBy = setSortingCriteria(criteria, pageable.getSort());

        queryFactory.select(qCrewsUsers.crewsUsersPk.crew)
                .from(qCrewsUsers)
                .where()
                .orderBy()
                .fetch();


        return List.of();
    }

//    private OrderSpecifier<?> setSortingCriteria(SearchCriteria criteria, Sort sort){
//        switch (criteria){
//            case NAME ->
//            {
//                return sort.get()== ?QCREW.crewName.asc();
//            }
//            case MEMBER ->{
//                return
//            }
//            default -> {
//                return;
//            }
//        }

//    }


}
