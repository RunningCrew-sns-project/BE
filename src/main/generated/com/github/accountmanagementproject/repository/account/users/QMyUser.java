package com.github.accountmanagementproject.repository.account.users;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMyUser is a Querydsl query type for MyUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMyUser extends EntityPathBase<MyUser> {

    private static final long serialVersionUID = 625112283L;

    public static final QMyUser myUser = new QMyUser("myUser");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final ListPath<com.github.accountmanagementproject.repository.crew_join_post.CrewJoinPost, com.github.accountmanagementproject.repository.crew_join_post.QCrewJoinPost> crewJoinPostList = this.<com.github.accountmanagementproject.repository.crew_join_post.CrewJoinPost, com.github.accountmanagementproject.repository.crew_join_post.QCrewJoinPost>createList("crewJoinPostList", com.github.accountmanagementproject.repository.crew_join_post.CrewJoinPost.class, com.github.accountmanagementproject.repository.crew_join_post.QCrewJoinPost.class, PathInits.DIRECT2);

    public final SetPath<com.github.accountmanagementproject.repository.crew_join_post.Crew, com.github.accountmanagementproject.repository.crew_join_post.QCrew> crews = this.<com.github.accountmanagementproject.repository.crew_join_post.Crew, com.github.accountmanagementproject.repository.crew_join_post.QCrew>createSet("crews", com.github.accountmanagementproject.repository.crew_join_post.Crew.class, com.github.accountmanagementproject.repository.crew_join_post.QCrew.class, PathInits.DIRECT2);

    public final DatePath<java.time.LocalDate> dateOfBirth = createDate("dateOfBirth", java.time.LocalDate.class);

    public final StringPath email = createString("email");

    public final NumberPath<Integer> failureCount = createNumber("failureCount", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> failureDate = createDateTime("failureDate", java.time.LocalDateTime.class);

    public final EnumPath<com.github.accountmanagementproject.repository.account.users.enums.Gender> gender = createEnum("gender", com.github.accountmanagementproject.repository.account.users.enums.Gender.class);

    public final DateTimePath<java.time.LocalDateTime> lastLogin = createDateTime("lastLogin", java.time.LocalDateTime.class);

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath profileImg = createString("profileImg");

    public final SetPath<com.github.accountmanagementproject.repository.account.users.roles.Role, com.github.accountmanagementproject.repository.account.users.roles.QRole> roles = this.<com.github.accountmanagementproject.repository.account.users.roles.Role, com.github.accountmanagementproject.repository.account.users.roles.QRole>createSet("roles", com.github.accountmanagementproject.repository.account.users.roles.Role.class, com.github.accountmanagementproject.repository.account.users.roles.QRole.class, PathInits.DIRECT2);

    public final NumberPath<java.math.BigInteger> socialId = createNumber("socialId", java.math.BigInteger.class);

    public final EnumPath<com.github.accountmanagementproject.repository.account.users.enums.UserStatus> status = createEnum("status", com.github.accountmanagementproject.repository.account.users.enums.UserStatus.class);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public final SetPath<com.github.accountmanagementproject.repository.userLikesBlog.UserLikesBlog, com.github.accountmanagementproject.repository.userLikesBlog.QUserLikesBlog> userLikesBlogs = this.<com.github.accountmanagementproject.repository.userLikesBlog.UserLikesBlog, com.github.accountmanagementproject.repository.userLikesBlog.QUserLikesBlog>createSet("userLikesBlogs", com.github.accountmanagementproject.repository.userLikesBlog.UserLikesBlog.class, com.github.accountmanagementproject.repository.userLikesBlog.QUserLikesBlog.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> withdrawalDate = createDateTime("withdrawalDate", java.time.LocalDateTime.class);

    public QMyUser(String variable) {
        super(MyUser.class, forVariable(variable));
    }

    public QMyUser(Path<? extends MyUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMyUser(PathMetadata metadata) {
        super(MyUser.class, metadata);
    }

}

