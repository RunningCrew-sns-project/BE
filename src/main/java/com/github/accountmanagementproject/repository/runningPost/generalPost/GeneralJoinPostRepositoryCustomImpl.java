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
import com.querydsl.jpa.JPQLSubQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @Override
    public boolean isPostAuthor(Long postId, String authorEmail, boolean isCrew) {
        String authorEmailFromDb = isCrew
                ? queryFactory.select(qCrewJoinPost.author.email)
                .from(qCrewJoinPost)
                .where(qCrewJoinPost.crewPostId.eq(postId))
                .fetchOne()
                : queryFactory.select(qGeneralJoinPost.author.email)
                .from(qGeneralJoinPost)
                .where(qGeneralJoinPost.generalPostId.eq(postId))
                .fetchOne();

        return authorEmailFromDb != null && authorEmailFromDb.equals(authorEmail);
    }

    @Override
    public boolean deleteMember(Long postId, Long badUserId, boolean isCrew) {
        long result = isCrew ?
                queryFactory.update(qCrewRunGroup)
                        .where(qCrewRunGroup.crewJoinPost.crewPostId.eq(postId)
                                .and(qCrewRunGroup.user.userId.eq(badUserId))
                                .and(qCrewRunGroup.status.eq(ParticipationStatus.APPROVED)))
                        .set(qCrewRunGroup.status, ParticipationStatus.FORCED_EXIT)
                        .execute() :
                queryFactory.update(qRunGroup)
                        .where(qRunGroup.generalJoinPost.generalPostId.eq(postId)
                                .and(qRunGroup.user.userId.eq(badUserId))
                                .and(qRunGroup.status.eq(ParticipationStatus.APPROVED)))
                        .set(qRunGroup.status, ParticipationStatus.FORCED_EXIT)
                        .execute();
        return result == 1;
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
    private JPQLSubQuery<Long> memberCountSubQuery(boolean isCrew, boolean isApproved) {
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
        QGeneralJoinPost post = QGeneralJoinPost.generalJoinPost;
        QRunJoinPostImage qrunJoinPostImage = QRunJoinPostImage.runJoinPostImage;
        QRunGroup qRunGroup = QRunGroup.runGroup;

        List<GeneralJoinPost> posts = queryFactory
                .selectFrom(post)
                .where(
                        dateCondition(post, date),
                        locationCondition(post, location),
                        cursorCondition(post, cursor, sortType)
                )
                .leftJoin(post.generalJoinPostImages, qrunJoinPostImage).fetchJoin()
                .orderBy(getOrderSpecifier(sortType, post))
                .limit(size + 1) // 요청한 size보다 1개 더 가져와서 다음 데이터 확인
                .fetch();

        posts.forEach(this::setApprovedParticipantCount);

        System.out.println("Query result size: {}" + posts.size());  // 로그 추가
        if (!posts.isEmpty()) {
            System.out.println("First post ID: {}, Last post ID: {}" +
                    posts.get(0).getGeneralPostId() +
                    posts.get(posts.size()-1).getGeneralPostId());
        }
        return posts;
    }


    private BooleanExpression dateCondition(QGeneralJoinPost post, LocalDate date) {
        return date != null ? post.date.eq(date) : null;
    }

    private BooleanExpression locationCondition(QGeneralJoinPost post, String location) {
        if (location == null || location.trim().isEmpty() || location.equals("전체")) {
            return null;
        }
        return post.location.eq(location);
    }

    private BooleanExpression cursorCondition(QGeneralJoinPost post, Integer cursor, String sortType) {
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
            QGeneralJoinPost post,
            LocalDate cursorDate,
            LocalTime cursorTime,
            Integer cursor) {
        return post.date.gt(cursorDate)
                .or(post.date.eq(cursorDate)
                        .and(post.startTime.gt(cursorTime))
                        .or(post.date.eq(cursorDate)
                                .and(post.startTime.eq(cursorTime))
                                .and(post.generalPostId.goe(cursor))));
    }

    private BooleanExpression createNewestCursorCondition(
            QGeneralJoinPost post,
            LocalDate cursorDate,
            LocalTime cursorTime,
            Integer cursor) {
        return post.date.lt(cursorDate)
                .or(post.date.eq(cursorDate)
                        .and(post.startTime.lt(cursorTime))
                        .or(post.date.eq(cursorDate)
                                .and(post.startTime.eq(cursorTime))
                                .and(post.generalPostId.loe(cursor))));
    }


    // 커서 ID의 날짜를 조회하는 메서드 추가
    private LocalDate getPostDate(Integer cursor) {
        QGeneralJoinPost post = QGeneralJoinPost.generalJoinPost;
        try {
            return queryFactory
                    .select(post.date)
                    .from(post)
                    .where(post.generalPostId.eq(Long.valueOf(cursor)))
                    .fetchOne();
        } catch (Exception e) {
//            log.error("Error fetching date for cursor: {}", cursor, e);
            return null;
        }
    }

    // 커서 ID의 시간을 조회하는 메서드 추가
    private LocalTime getPostTime(Integer cursor) {
        QGeneralJoinPost post = QGeneralJoinPost.generalJoinPost;
        try {
            return queryFactory
                    .select(post.startTime)
                    .from(post)
                    .where(post.generalPostId.eq(Long.valueOf(cursor)))
                    .fetchOne();
        } catch (Exception e) {
//            log.error("Error fetching time for cursor: {}", cursor, e);
            return null;
        }
    }


    // 약속 날짜 기준 최신순, 오래된순
    private OrderSpecifier<?>[] getOrderSpecifier(String sortType, QGeneralJoinPost post) {
        if (sortType.equalsIgnoreCase("oldest")) {
            return new OrderSpecifier[]{
                    post.date.asc(),
                    post.startTime.asc(),
                    post.generalPostId.asc()
            };
        }
        return new OrderSpecifier[]{
                post.date.desc(),
                post.startTime.desc(),  // asc에서 desc로 수정
                post.generalPostId.desc()
        };
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
