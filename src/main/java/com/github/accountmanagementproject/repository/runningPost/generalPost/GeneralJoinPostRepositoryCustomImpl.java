package com.github.accountmanagementproject.repository.runningPost.generalPost;

import com.github.accountmanagementproject.repository.runningPost.image.QRunJoinPostImage;
import com.github.accountmanagementproject.repository.runningPost.runGroup.QRunGroup;
import com.github.accountmanagementproject.repository.runningPost.enums.ParticipationStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.util.List;

public class GeneralJoinPostRepositoryCustomImpl implements GeneralJoinPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public GeneralJoinPostRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }


    @Override
    public List<GeneralJoinPost> findFilteredPosts(LocalDate date, String location, Integer cursor, int size, String sortType) {
        QGeneralJoinPost generalJoinPost = QGeneralJoinPost.generalJoinPost;
        QRunJoinPostImage qrunJoinPostImage = QRunJoinPostImage.runJoinPostImage;
        QRunGroup qRunGroup = QRunGroup.runGroup;

        BooleanExpression dateCondition = date != null ? generalJoinPost.date.goe(date) : null;
        BooleanExpression locationCondition = location != null && !location.trim().isEmpty() && !location.equals("전체")
                ? generalJoinPost.location.eq(location) : null;
        // cursor 값이 너무 작은 경우 무시하거나, 적절한 값으로 조정
        BooleanExpression cursorCondition = (cursor != null && cursor > 10000) ? generalJoinPost.generalPostId.lt(cursor) : null;

        List<GeneralJoinPost> posts = queryFactory
                .selectFrom(generalJoinPost)
                .where(dateCondition, locationCondition, cursorCondition)
                .leftJoin(generalJoinPost.generalJoinPostImages, qrunJoinPostImage).fetchJoin()
                .orderBy(getOrderSpecifier(sortType, generalJoinPost))
                .limit(size + 1) // 요청한 size보다 1개 더 가져와서 다음 데이터 확인
                .fetch();

        posts.forEach(this::setApprovedParticipantCount);

        // 다음 페이지 여부 확인을 위해 추가로 가져온 데이터 제거
        if (posts.size() > size) {
            posts.remove(posts.size() - 1);
        }

        return posts;
    }


    // 약속 날짜 기준 최신순, 오래된순
    private OrderSpecifier<?> getOrderSpecifier(String sortType, QGeneralJoinPost generalJoinPost) {
        return sortType.equalsIgnoreCase("oldest")
                ? generalJoinPost.date.asc()
                : generalJoinPost.date.desc();
    }

    // 참여자 수
    private void setApprovedParticipantCount(GeneralJoinPost post) {
        QRunGroup qRunGroup = QRunGroup.runGroup;

        Long count = queryFactory
                .select(qRunGroup.count())
                .from(qRunGroup)
                .where(qRunGroup.generalJoinPost.eq(post)
                        .and(qRunGroup.status.eq(ParticipationStatus.APPROVED)))
                .fetchOne();

        post.setCurrentPeople(count != null ? count.intValue() : 0);
    }

}
