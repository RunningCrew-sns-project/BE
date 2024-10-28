package com.github.accountmanagementproject.repository.crew_join_post.repository;

import com.github.accountmanagementproject.repository.crew_join_post.CrewJoinPost;
import com.github.accountmanagementproject.repository.crew_join_post.QCrewJoinPost;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;


public class CrewJoinPostRepositoryCustomImpl implements CrewJoinPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CrewJoinPostRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    // location 필터를 **시작 위치(inputAddress)**를 기준으로 모두 불러오기
    @Override
    public Slice<CrewJoinPost> findByLocation(String location, Pageable pageable) {
        QCrewJoinPost qCrewJoinPost = QCrewJoinPost.crewJoinPost;

        // location 을 기준으로 inputAddress 필터링
        BooleanExpression locationFilter = location != null && !location.equals("전체") ?
                qCrewJoinPost.inputAddress.contains(location) : null;

        List<CrewJoinPost> posts = queryFactory.selectFrom(qCrewJoinPost)
                .where(locationFilter)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = posts.size() > pageable.getPageSize();
        if (hasNext) {
            posts.remove(posts.size() - 1); // 실제 응답에는 다음 페이지 확인 용도로 가져온 한 건 제거
        }

        return new SliceImpl<>(posts, pageable, hasNext);
    }
}
