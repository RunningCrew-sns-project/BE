package com.github.accountmanagementproject.repository.runningPost.crewPost;

import com.github.accountmanagementproject.repository.runningPost.generalPost.QGeneralJoinPost;
import com.github.accountmanagementproject.repository.runningPost.image.QCrewJoinPostImage;
import com.github.accountmanagementproject.repository.runningPost.image.QRunJoinPostImage;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;


@Slf4j
public class CrewJoinPostRepositoryCustomImpl implements CrewJoinPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    QCrewJoinPost crewJoinPost = QCrewJoinPost.crewJoinPost;
    QCrewJoinPostImage crewJoinPostImage = QCrewJoinPostImage.crewJoinPostImage;

    public CrewJoinPostRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }


    // 크루 달리기 메시물
    @Override
    public List<CrewJoinPost> findFilteredPosts(LocalDate date, String location, Integer cursor, int size, String sortType) {

        // 조건에 따른 BooleanExpression 설정
        BooleanExpression dateCondition = date != null ? crewJoinPost.date.goe(date) : null;
        BooleanExpression locationCondition = null;
        if (location != null && !location.trim().isEmpty() && !location.equals("전체")) {
            locationCondition = crewJoinPost.location.eq(location);
        }
        BooleanExpression cursorCondition = cursor != null ? crewJoinPost.crewPostId.lt(cursor) : null;

        List<CrewJoinPost> posts = queryFactory.selectFrom(crewJoinPost)
                .where(dateCondition, locationCondition, cursorCondition) // cursorCondition 추가
                .leftJoin(crewJoinPost.crewJoinPostImages, crewJoinPostImage).fetchJoin()
                .orderBy(getOrderSpecifier(sortType, crewJoinPost))
                .limit(size + 1) // 요청한 size보다 1개 더 가져와서 다음 데이터 확인
                .fetch();

        return posts;
    }

    // 약속 날짜 기준 최신순, 오래된순
    private OrderSpecifier<?> getOrderSpecifier(String sortType, QCrewJoinPost crewJoinPost) {
        return sortType.equalsIgnoreCase("oldest")
                ? crewJoinPost.date.asc()
                : crewJoinPost.date.desc();
    }


    // 크루 상세 페이지에 노출되는 목록
    @Override
    public List<CrewJoinPost> findFilteredCrewPosts(Long crewId, LocalDate date, String location, Integer cursor, int size, String sortType) {
        System.out.println("Input parameters:");
        System.out.println("crewId: " + crewId);
        System.out.println("date: " + date);
        System.out.println("location: " + location);
        System.out.println("cursor: " + cursor);
        System.out.println("size: " + size);
        System.out.println("sortType: " + sortType);

        // 조건에 따른 BooleanExpression 설정
        BooleanExpression crewCondition = crewId != null ? crewJoinPost.crew.crewId.eq(crewId) : null;
        BooleanExpression dateCondition = date != null ? crewJoinPost.date.goe(date) : null;
        BooleanExpression locationCondition = null;
        if (location != null && !location.trim().isEmpty() && !location.equals("전체")) {
            locationCondition = crewJoinPost.location.eq(location);
        }
        // cursor 값이 너무 작은 경우 무시하거나, 적절한 값으로 조정
        BooleanExpression cursorCondition = (cursor != null && cursor > 10000) ? crewJoinPost.crewPostId.lt(cursor) : null;

        List<CrewJoinPost> posts = queryFactory.selectFrom(crewJoinPost)
                .where(crewCondition, dateCondition, locationCondition, cursorCondition) // cursorCondition 추가
                .leftJoin(crewJoinPost.crewJoinPostImages, crewJoinPostImage).fetchJoin()
                .orderBy(getOrderSpecifier(sortType, crewJoinPost))
                .limit(size + 1) // 요청한 size보다 1개 더 가져와서 다음 데이터 확인
                .fetch();
        System.out.println("Found posts size: " + posts.size());

        return posts;
    }


}
