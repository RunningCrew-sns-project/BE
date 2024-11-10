package com.github.accountmanagementproject.repository.account.friendship;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFriendship is a Querydsl query type for Friendship
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFriendship extends EntityPathBase<Friendship> {

    private static final long serialVersionUID = -1450997296L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFriendship friendship = new QFriendship("friendship");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> friendshipId = createNumber("friendshipId", Integer.class);

    public final com.github.accountmanagementproject.repository.account.user.QMyUser myUser1;

    public final com.github.accountmanagementproject.repository.account.user.QMyUser myUser2;

    public final EnumPath<FriendshipStatus> status = createEnum("status", FriendshipStatus.class);

    public QFriendship(String variable) {
        this(Friendship.class, forVariable(variable), INITS);
    }

    public QFriendship(Path<? extends Friendship> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFriendship(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFriendship(PathMetadata metadata, PathInits inits) {
        this(Friendship.class, metadata, inits);
    }

    public QFriendship(Class<? extends Friendship> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.myUser1 = inits.isInitialized("myUser1") ? new com.github.accountmanagementproject.repository.account.user.QMyUser(forProperty("myUser1")) : null;
        this.myUser2 = inits.isInitialized("myUser2") ? new com.github.accountmanagementproject.repository.account.user.QMyUser(forProperty("myUser2")) : null;
    }

}

