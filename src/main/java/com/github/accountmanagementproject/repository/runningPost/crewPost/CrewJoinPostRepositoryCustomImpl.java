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
    public Slice<CrewJoinPost> findFilteredPosts(LocalDate date, String location, Pageable pageable) {

        QCrewJoinPost crewJoinPost = QCrewJoinPost.crewJoinPost;
        QRunJoinPostImage runJoinPostImage = QRunJoinPostImage.runJoinPostImage;

        BooleanExpression dateCondition = date != null ? crewJoinPost.createdAt.goe(date.atStartOfDay()) : null;
        BooleanExpression locationCondition = null;
        if (location != null && !location.trim().isEmpty() && !location.equals("전체")) {
            locationCondition = crewJoinPost.location.eq(location);
        }

        List<CrewJoinPost> posts = queryFactory.selectFrom(crewJoinPost)
                .where(dateCondition, locationCondition)
                .leftJoin(crewJoinPost.crewJoinPostImages, runJoinPostImage).fetchJoin()  // fetch join 추가
                .orderBy(crewJoinPost.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)  // 다음 페이지 존재 여부 확인을 위해 limit을 한 개 더 가져옴
                .fetch();

        System.out.println("posts : " + posts);

        boolean hasNext = posts.size() > pageable.getPageSize();
        if (hasNext) {
            posts.remove(posts.size() - 1); // 실제 응답에는 다음 페이지 확인 용도로 가져온 한 건 제거
        }

        return new SliceImpl<>(posts, pageable, hasNext);
    }
}
