package com.github.accountmanagementproject.repository.runningPost.generalPost;

import com.github.accountmanagementproject.repository.runningPost.crewPost.CrewJoinPost;
import com.github.accountmanagementproject.repository.runningPost.image.QRunJoinPostImage;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.util.List;

public class GeneralJoinPostRepositoryCustomImpl implements GeneralJoinPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public GeneralJoinPostRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }


    @Override
    public List<GeneralJoinPost> findFilteredPosts(LocalDate date, String location, Integer cursor, int size) {
        QGeneralJoinPost generalJoinPost = QGeneralJoinPost.generalJoinPost;
        QRunJoinPostImage qrunJoinPostImage = QRunJoinPostImage.runJoinPostImage;

        // 필터 조건 설정
        BooleanExpression dateCondition = date != null ? generalJoinPost.createdAt.goe(date.atStartOfDay()) : null;
        BooleanExpression locationCondition = null;
        if (location != null && !location.trim().isEmpty() && !location.equals("전체")) {
            locationCondition = generalJoinPost.location.eq(location);
        }
        BooleanExpression cursorCondition = cursor != null ? generalJoinPost.generalPostId.lt(cursor) : null;

        // 쿼리 실행
        List<GeneralJoinPost> posts = queryFactory.selectFrom(generalJoinPost)
                .where(dateCondition, locationCondition, cursorCondition)
                .leftJoin(generalJoinPost.generalJoinPostImages, qrunJoinPostImage).fetchJoin()
                .orderBy(generalJoinPost.createdAt.desc())
                .limit(size + 1) // 요청한 size보다 1개 더 가져와서 다음 데이터 확인
                .fetch();

        return posts;
    }
}
