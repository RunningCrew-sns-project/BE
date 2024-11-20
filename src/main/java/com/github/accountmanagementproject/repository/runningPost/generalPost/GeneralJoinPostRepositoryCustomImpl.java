package com.github.accountmanagementproject.repository.runningPost.generalPost;

import com.github.accountmanagementproject.repository.account.user.QMyUser;
import com.github.accountmanagementproject.repository.runningPost.crewPost.QCrewJoinPost;
import com.github.accountmanagementproject.repository.runningPost.crewRunGroup.QCrewRunGroup;
import com.github.accountmanagementproject.repository.runningPost.enums.ParticipationStatus;
import com.github.accountmanagementproject.repository.runningPost.image.QRunJoinPostImage;
import com.github.accountmanagementproject.repository.runningPost.runGroup.QRunGroup;
import com.github.accountmanagementproject.web.dto.runJoinPost.RunPostAndMemberResponse;
import com.github.accountmanagementproject.web.dto.runJoinPost.runmember.RunMemberResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneralJoinPostRepositoryCustomImpl implements GeneralJoinPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public GeneralJoinPostRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    //temp //temp //temp
    // temp
    private final QCrewJoinPost qCrewJoinPost = QCrewJoinPost.crewJoinPost;
    private final QGeneralJoinPost qGeneralJoinPost = QGeneralJoinPost.generalJoinPost;
    private final QRunGroup qRunGroup = QRunGroup.runGroup;
    private final QCrewRunGroup qCrewRunGroup = QCrewRunGroup.crewRunGroup;


    @Override
    public List<RunPostAndMemberResponse> findPostAndMembers(String email, boolean isCrew, Boolean isAll) {

        BooleanBuilder whereClause = makeBooleanBuilder(email, isAll, isCrew);
        if(isCrew){
            List<RunPostAndMemberResponse> posts = crewJoinPostQuery(whereClause);
            Map<Long, List<RunMemberResponse>> member = runMemberQuery(whereClause);
            posts.forEach(post->
                post.setRunMemberResponses(member.getOrDefault(post.getRunPostId(), Collections.emptyList())));
            posts.sort(Comparator.comparing(run->run.getRunMemberResponses().get(0).getApplicationDate(), Comparator.reverseOrder()));
            return posts;
        }else{
            List<RunPostAndMemberResponse> posts = generalJoinPostQuery(whereClause);
            Map<Long, List<RunMemberResponse>> member = generalRunMemberQuery(whereClause);
            posts.forEach(post->
                post.setRunMemberResponses(member.getOrDefault(post.getRunPostId(), Collections.emptyList())));
            posts.sort(Comparator.comparing(run->run.getRunMemberResponses().get(0).getApplicationDate(), Comparator.reverseOrder()));
            return posts;
        }



    }

    private Map<Long, List<RunMemberResponse>> generalRunMemberQuery(BooleanBuilder whereClause) {
        List<RunMemberResponse> member = queryFactory.select(Projections.fields(RunMemberResponse.class,
                        qGeneralJoinPost.generalPostId.as("postId"),
                        qRunGroup.user.userId,
                        qRunGroup.user.nickname,
                        qRunGroup.user.profileImg,
                        qRunGroup.user.profileMessage,
                        qRunGroup.user.gender,
                        qRunGroup.user.phoneNumber,
                        qRunGroup.status,
                        qRunGroup.joinedAt.as("applicationDate")))
                .from(qRunGroup)
                .join(qRunGroup.user, QMyUser.myUser)
                .join(qRunGroup.generalJoinPost, qGeneralJoinPost)
                .where(whereClause)
                .orderBy(qRunGroup.joinedAt.desc())
                .fetch();

        return member.stream()
                .collect(Collectors.groupingBy(RunMemberResponse::getPostId));
    }
    private JPQLQuery<Long> memberCountSubQuery(boolean isCrew, boolean isApproved) {
        BooleanExpression where = isCrew ?
                qCrewRunGroup.crewJoinPost.eq(qCrewJoinPost) :
                qRunGroup.generalJoinPost.eq(qGeneralJoinPost);
        return isCrew ?
                JPAExpressions.select(qCrewRunGroup.count())
                        .from(qCrewRunGroup)
                        .where(isApproved ? where.and(qCrewRunGroup.status.eq(ParticipationStatus.APPROVED)) : where) :
                JPAExpressions.select(qRunGroup.count())
                        .from(qRunGroup)
                        .where(isApproved ? where.and(qRunGroup.status.eq(ParticipationStatus.APPROVED)) : where);
    }

    private List<RunPostAndMemberResponse> generalJoinPostQuery(BooleanBuilder whereClause) {
        QMyUser author = QMyUser.myUser;
        return queryFactory.select(Projections.fields(RunPostAndMemberResponse.class,
                        qGeneralJoinPost.generalPostId.as("runPostId"),
                        qGeneralJoinPost.title,
                        qGeneralJoinPost.maximumPeople.as("maxParticipant"),
                        qGeneralJoinPost.location,
                        ExpressionUtils.as(memberCountSubQuery(false, true), "currentParticipant")))
                .from(qGeneralJoinPost)
                .join(qGeneralJoinPost.author, author)
                .leftJoin(qRunGroup).on(qRunGroup.generalJoinPost.eq(qGeneralJoinPost))
                .where(whereClause, memberCountSubQuery(false, false).gt(0L))
                .groupBy(qGeneralJoinPost)
                .fetch();


    }

    private BooleanBuilder makeBooleanBuilder(String email, Boolean isAll, boolean isCrew) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(isCrew ?
                qCrewJoinPost.author.email.eq(email) :
                qGeneralJoinPost.author.email.eq(email));
        if (isAll == null)
            builder.and(isCrew ?
                    qCrewRunGroup.status.eq(ParticipationStatus.APPROVED) :
                    qRunGroup.status.eq(ParticipationStatus.APPROVED));
        else if (!isAll)
            builder.and(isCrew ?
                    qCrewRunGroup.status.eq(ParticipationStatus.PENDING) :
                    qRunGroup.status.eq(ParticipationStatus.PENDING));

        return builder;
    }

    private Map<Long, List<RunMemberResponse>> runMemberQuery(BooleanBuilder whereClause) {
        List<RunMemberResponse> member = queryFactory.select(Projections.fields(RunMemberResponse.class,
                        qCrewJoinPost.crewPostId.as("postId"),
                        qCrewRunGroup.user.userId,
                        qCrewRunGroup.user.nickname,
                        qCrewRunGroup.user.profileImg,
                        qCrewRunGroup.user.profileMessage,
                        qCrewRunGroup.user.gender,
                        qCrewRunGroup.user.phoneNumber,
                        qCrewRunGroup.status,
                        qCrewRunGroup.joinedAt.as("applicationDate")))
                .from(qCrewRunGroup)
                .join(qCrewRunGroup.user, QMyUser.myUser)
                .join(qCrewRunGroup.crewJoinPost, qCrewJoinPost)
                .where(whereClause)
                .orderBy(qCrewRunGroup.joinedAt.desc())
                .fetch();

        return member.stream()
                .collect(Collectors.groupingBy(RunMemberResponse::getPostId));
    }

    private List<RunPostAndMemberResponse> crewJoinPostQuery(BooleanBuilder whereClause) {
        QMyUser author = QMyUser.myUser;

        return queryFactory
                .select(Projections.fields(RunPostAndMemberResponse.class,
                        qCrewJoinPost.crewPostId.as("runPostId"),
                        qCrewJoinPost.title,
                        qCrewJoinPost.maximumPeople.as("maxParticipant"),
                        qCrewJoinPost.location,
                        ExpressionUtils.as(memberCountSubQuery(true, true), "currentParticipant")))
                .from(qCrewJoinPost)
                .join(qCrewJoinPost.author, author)
                .leftJoin(qCrewRunGroup).on(qCrewRunGroup.crewJoinPost.eq(qCrewJoinPost))
                .where(whereClause.and(memberCountSubQuery(true, false).gt(0L)))
                .groupBy(qCrewJoinPost)
                .fetch();
    }


    //temp
    //temp
    //temp


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
