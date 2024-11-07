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
    public Slice<GeneralJoinPost> findFilteredPosts(LocalDate date, String location, Pageable pageable) {
        QGeneralJoinPost generalJoinPost = QGeneralJoinPost.generalJoinPost;
        QRunJoinPostImage runJoinPostImage = QRunJoinPostImage.runJoinPostImage;

        BooleanExpression dateCondition = date != null ? generalJoinPost.createdAt.goe(date.atStartOfDay()) : null;
        BooleanExpression locationCondition = null;
        if (location != null && !location.trim().isEmpty() && !location.equals("전체")) {
            locationCondition = generalJoinPost.location.eq(location);
        }

        List<GeneralJoinPost> posts = queryFactory.selectFrom(generalJoinPost)
                .where(dateCondition, locationCondition)
                .leftJoin(generalJoinPost.generalJoinPostImages, runJoinPostImage).fetchJoin()  // fetch join 추가
                .orderBy(generalJoinPost.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)  // 다음 페이지 존재 여부 확인을 위해 limit을 한 개 더 가져옴
                .fetch();

        boolean hasNext = posts.size() > pageable.getPageSize();
        if (hasNext) {
            posts.remove(posts.size() - 1); // 실제 응답에는 다음 페이지 확인 용도로 가져온 한 건 제거
        }

        return new SliceImpl<>(posts, pageable, hasNext);
    }
}
