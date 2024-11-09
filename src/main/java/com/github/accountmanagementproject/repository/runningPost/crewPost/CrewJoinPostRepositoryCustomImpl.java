package com.github.accountmanagementproject.repository.runningPost.crewPost;

import com.github.accountmanagementproject.repository.runningPost.crewPost.QCrewJoinPost;
import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;
import com.github.accountmanagementproject.repository.runningPost.image.QRunJoinPostImage;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.util.List;

public class CrewJoinPostRepositoryCustomImpl implements CrewJoinPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CrewJoinPostRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }


    @Override
    public List<CrewJoinPost> findFilteredPosts(LocalDate date, String location, Integer cursor, int size) {

        QCrewJoinPost crewJoinPost = QCrewJoinPost.crewJoinPost;
        QRunJoinPostImage runJoinPostImage = QRunJoinPostImage.runJoinPostImage;

        // 조건에 따른 BooleanExpression 설정
        BooleanExpression dateCondition = date != null ? crewJoinPost.createdAt.goe(date.atStartOfDay()) : null;
        BooleanExpression locationCondition = null;
        if (location != null && !location.trim().isEmpty() && !location.equals("전체")) {
            locationCondition = crewJoinPost.location.eq(location);
        }
        BooleanExpression cursorCondition = cursor != null ? crewJoinPost.crewPostId.lt(cursor) : null;

        // 쿼리 실행
        List<CrewJoinPost> posts = queryFactory.selectFrom(crewJoinPost)
                .where(dateCondition, locationCondition, cursorCondition) // cursorCondition 추가
                .leftJoin(crewJoinPost.crewJoinPostImages, runJoinPostImage).fetchJoin()
                .orderBy(crewJoinPost.createdAt.desc())
                .limit(size + 1) // 요청한 size보다 1개 더 가져와서 다음 데이터 확인
                .fetch();

        return posts;
    }
}
