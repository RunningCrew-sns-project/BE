package com.github.accountmanagementproject.repository.account.socialIds;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSocialId is a Querydsl query type for SocialId
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSocialId extends EntityPathBase<SocialId> {

    private static final long serialVersionUID = -2129972215L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSocialId socialId = new QSocialId("socialId");

    public final DateTimePath<java.time.LocalDateTime> connectAt = createDateTime("connectAt", java.time.LocalDateTime.class);

    public final com.github.accountmanagementproject.repository.account.users.QMyUser myUser;

    public final QSocialIdPk socialIdPk;

    public QSocialId(String variable) {
        this(SocialId.class, forVariable(variable), INITS);
    }

    public QSocialId(Path<? extends SocialId> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSocialId(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSocialId(PathMetadata metadata, PathInits inits) {
        this(SocialId.class, metadata, inits);
    }

    public QSocialId(Class<? extends SocialId> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.myUser = inits.isInitialized("myUser") ? new com.github.accountmanagementproject.repository.account.users.QMyUser(forProperty("myUser")) : null;
        this.socialIdPk = inits.isInitialized("socialIdPk") ? new QSocialIdPk(forProperty("socialIdPk")) : null;
    }

}

