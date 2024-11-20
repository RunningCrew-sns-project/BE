package com.github.accountmanagementproject.repository.runningPost.crewPost;

import com.github.accountmanagementproject.repository.runningPost.generalPost.QGeneralJoinPost;
import com.github.accountmanagementproject.repository.runningPost.image.QCrewJoinPostImage;
import com.github.accountmanagementproject.repository.runningPost.image.QRunJoinPostImage;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Slf4j
public class CrewJoinPostRepositoryCustomImpl implements CrewJoinPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    QCrewJoinPost post = QCrewJoinPost.crewJoinPost;
    QCrewJoinPostImage crewJoinPostImage = QCrewJoinPostImage.crewJoinPostImage;

    public CrewJoinPostRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }


    // 크루 달리기 메시물
    @Override
    public List<CrewJoinPost> findFilteredPosts(LocalDate date, String location, Integer cursor, int size, String sortType) {

        QCrewJoinPost post = QCrewJoinPost.crewJoinPost;
        QCrewJoinPostImage crewJoinPostImage = QCrewJoinPostImage.crewJoinPostImage;

        List<CrewJoinPost> posts = queryFactory
                .selectFrom(post)
                .where(
                        dateCondition(post, date),
                        locationCondition(post, location),
                        cursorCondition(post, cursor, sortType)
                )
                .leftJoin(QCrewJoinPost.crewJoinPost.crewJoinPostImages, QCrewJoinPostImage.crewJoinPostImage).fetchJoin()
                .orderBy(getOrderSpecifier(sortType, post))
                .limit(size + 1) // 요청한 size보다 1개 더 가져와서 다음 데이터 확인
                .fetch();

        return posts;
    }

    private BooleanExpression dateCondition(QCrewJoinPost post, LocalDate date) {
        return date != null ? post.date.eq(date) : null;
    }

    private BooleanExpression locationCondition(QCrewJoinPost post, String location) {
        if (location == null || location.trim().isEmpty() || location.equals("전체")) {
            return null;
        }
        return post.location.eq(location);
    }

    private BooleanExpression cursorCondition(QCrewJoinPost post, Integer cursor, String sortType) {
        if (cursor == null) {
            return null;
        }

        LocalDate cursorDate = getPostDate(cursor);
        LocalTime cursorTime = getPostTime(cursor);

        return sortType.equalsIgnoreCase("oldest")
                ? createOldestCursorCondition(post, cursorDate, cursorTime, cursor)
                : createNewestCursorCondition(post, cursorDate, cursorTime, cursor);
    }

    private BooleanExpression createOldestCursorCondition(
            QCrewJoinPost post,
            LocalDate cursorDate,
            LocalTime cursorTime,
            Integer cursor) {
        return post.date.gt(cursorDate)
                .or(post.date.eq(cursorDate)
                        .and(post.startTime.gt(cursorTime))
                        .or(post.date.eq(cursorDate)
                                .and(post.startTime.eq(cursorTime))
                                .and(post.crewPostId.goe(cursor))));
    }

    private BooleanExpression createNewestCursorCondition(
            QCrewJoinPost post,
            LocalDate cursorDate,
            LocalTime cursorTime,
            Integer cursor) {
        return post.date.lt(cursorDate)
                .or(post.date.eq(cursorDate)
                        .and(post.startTime.lt(cursorTime))
                        .or(post.date.eq(cursorDate)
                                .and(post.startTime.eq(cursorTime))
                                .and(post.crewPostId.loe(cursor))));
    }


    // 커서 ID의 날짜를 조회하는 메서드 추가
    private LocalDate getPostDate(Integer cursor) {
        QCrewJoinPost post = QCrewJoinPost.crewJoinPost;
        try {
            return queryFactory
                    .select(post.date)
                    .from(post)
                    .where(post.crewPostId.eq(Long.valueOf(cursor)))
                    .fetchOne();
        } catch (Exception e) {
//            log.error("Error fetching date for cursor: {}", cursor, e);
            return null;
        }
    }

    // 커서 ID의 시간을 조회하는 메서드 추가
    private LocalTime getPostTime(Integer cursor) {
        QCrewJoinPost post = QCrewJoinPost.crewJoinPost;
        try {
            return queryFactory
                    .select(post.startTime)
                    .from(post)
                    .where(post.crewPostId.eq(Long.valueOf(cursor)))
                    .fetchOne();
        } catch (Exception e) {
//            log.error("Error fetching time for cursor: {}", cursor, e);
            return null;
        }
    }


    // 약속 날짜 기준 최신순, 오래된순
    private OrderSpecifier<?>[] getOrderSpecifier(String sortType, QCrewJoinPost post) {
        if (sortType.equalsIgnoreCase("oldest")) {
            return new OrderSpecifier[]{
                    post.date.asc(),
                    post.startTime.asc(),
                    post.crewPostId.asc()
            };
        }
        return new OrderSpecifier[]{
                post.date.desc(),
                post.startTime.desc(),  // asc에서 desc로 수정
                post.crewPostId.desc()
        };
    }



    // 약속 날짜 기준 최신순, 오래된순
//    private OrderSpecifier<?> getOrderSpecifier(String sortType, QCrewJoinPost crewJoinPost) {
//        return sortType.equalsIgnoreCase("oldest")
//                ? crewJoinPost.date.asc()
//                : crewJoinPost.date.desc();
//    }


    // 크루 상세 페이지에 노출되는 목록
    @Override
    public List<CrewJoinPost> findFilteredCrewPosts(Long crewId, LocalDate date, String location, Integer cursor, int size, String sortType) {
        // 조건에 따른 BooleanExpression 설정
        BooleanExpression crewCondition = crewId != null ? post.crew.crewId.eq(crewId) : null;
//        BooleanExpression dateCondition = date != null ? post.date.eq(date) : null;
//        BooleanExpression locationCondition = null;
//        if (location != null && !location.trim().isEmpty() && !location.equals("전체")) {
//            locationCondition = post.location.eq(location);
//        }
        // cursor 값이 너무 작은 경우 무시하거나, 적절한 값으로 조정
//        BooleanExpression cursorCondition = (cursor != null && cursor > 10000) ? post.crewPostId.lt(cursor) : null;

        List<CrewJoinPost> posts = queryFactory.selectFrom(QCrewJoinPost.crewJoinPost)
                .where(
                        crewCondition,
                        dateCondition(QCrewJoinPost.crewJoinPost, date),
                        locationCondition(QCrewJoinPost.crewJoinPost, location),
                        cursorCondition(QCrewJoinPost.crewJoinPost, cursor, sortType)
                ) // cursorCondition 추가
                .leftJoin(QCrewJoinPost.crewJoinPost.crewJoinPostImages, QCrewJoinPostImage.crewJoinPostImage).fetchJoin()
                .orderBy(getOrderSpecifier(sortType, QCrewJoinPost.crewJoinPost))
                .limit(size + 1) // 요청한 size보다 1개 더 가져와서 다음 데이터 확인
                .fetch();
        System.out.println("Found posts size: " + posts.size());

        return posts;
    }


}
