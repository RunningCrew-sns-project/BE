package com.github.accountmanagementproject.repository.runningPost.repository;

import com.github.accountmanagementproject.repository.runningPost.QRunJoinPost;
import com.github.accountmanagementproject.repository.runningPost.RunJoinPost;
import com.github.accountmanagementproject.repository.runningPost.image.QRunJoinPostImage;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.util.List;

public class RunJoinPostRepositoryCustomImpl implements RunJoinPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public RunJoinPostRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }


    @Override
    public Slice<RunJoinPost> findPosts(Long crewId, LocalDate date, String location, Pageable pageable) {
        QRunJoinPost runJoinPost = QRunJoinPost.runJoinPost;
        QRunJoinPostImage runJoinPostImage = QRunJoinPostImage.runJoinPostImage;

        BooleanExpression crewIdCondition = crewId != null ? runJoinPost.crew.crewId.eq(crewId) : null;
        BooleanExpression dateCondition = date != null ? runJoinPost.createdAt.goe(date.atStartOfDay()) : null;
        BooleanExpression locationCondition = location != null && !location.equals("전체")
                ? runJoinPost.inputLocation.contains(location) : null;

        List<RunJoinPost> posts = queryFactory.selectFrom(runJoinPost)
                .where(crewIdCondition, dateCondition, locationCondition)
                .leftJoin(runJoinPost.joinPostImages, runJoinPostImage).fetchJoin()  // fetch join 추가
                .orderBy(runJoinPost.createdAt.desc())
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
