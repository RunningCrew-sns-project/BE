package com.github.accountmanagementproject.repository.account.user;

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

    private static final long serialVersionUID = -1962067716L;

    public static final QMyUser myUser = new QMyUser("myUser");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final ListPath<com.github.accountmanagementproject.repository.crew.crew.Crew, com.github.accountmanagementproject.repository.crew.crew.QCrew> crews = this.<com.github.accountmanagementproject.repository.crew.crew.Crew, com.github.accountmanagementproject.repository.crew.crew.QCrew>createList("crews", com.github.accountmanagementproject.repository.crew.crew.Crew.class, com.github.accountmanagementproject.repository.crew.crew.QCrew.class, PathInits.DIRECT2);

    public final DatePath<java.time.LocalDate> dateOfBirth = createDate("dateOfBirth", java.time.LocalDate.class);

    public final StringPath email = createString("email");

    public final NumberPath<Integer> failureCount = createNumber("failureCount", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> failureDate = createDateTime("failureDate", java.time.LocalDateTime.class);

    public final EnumPath<com.github.accountmanagementproject.repository.account.user.myenum.Gender> gender = createEnum("gender", com.github.accountmanagementproject.repository.account.user.myenum.Gender.class);

    public final DateTimePath<java.time.LocalDateTime> lastLogin = createDateTime("lastLogin", java.time.LocalDateTime.class);

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath profileImg = createString("profileImg");

    public final StringPath profileMessage = createString("profileMessage");

    public final SetPath<com.github.accountmanagementproject.repository.account.user.role.Role, com.github.accountmanagementproject.repository.account.user.role.QRole> roles = this.<com.github.accountmanagementproject.repository.account.user.role.Role, com.github.accountmanagementproject.repository.account.user.role.QRole>createSet("roles", com.github.accountmanagementproject.repository.account.user.role.Role.class, com.github.accountmanagementproject.repository.account.user.role.QRole.class, PathInits.DIRECT2);

    public final SetPath<com.github.accountmanagementproject.repository.account.socialid.SocialId, com.github.accountmanagementproject.repository.account.socialid.QSocialId> socialIds = this.<com.github.accountmanagementproject.repository.account.socialid.SocialId, com.github.accountmanagementproject.repository.account.socialid.QSocialId>createSet("socialIds", com.github.accountmanagementproject.repository.account.socialid.SocialId.class, com.github.accountmanagementproject.repository.account.socialid.QSocialId.class, PathInits.DIRECT2);

    public final EnumPath<com.github.accountmanagementproject.repository.account.user.myenum.UserStatus> status = createEnum("status", com.github.accountmanagementproject.repository.account.user.myenum.UserStatus.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

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

