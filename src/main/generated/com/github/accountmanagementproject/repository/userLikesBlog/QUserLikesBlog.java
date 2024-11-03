package com.github.accountmanagementproject.repository.userLikesBlog;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserLikesBlog is a Querydsl query type for UserLikesBlog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserLikesBlog extends EntityPathBase<UserLikesBlog> {

    private static final long serialVersionUID = -2107705829L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserLikesBlog userLikesBlog = new QUserLikesBlog("userLikesBlog");

    public final com.github.accountmanagementproject.repository.blog.QBlog blog;

    public final com.github.accountmanagementproject.repository.account.users.QMyUser user;

    public final NumberPath<Integer> userLikesBlogId = createNumber("userLikesBlogId", Integer.class);

    public QUserLikesBlog(String variable) {
        this(UserLikesBlog.class, forVariable(variable), INITS);
    }

    public QUserLikesBlog(Path<? extends UserLikesBlog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserLikesBlog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserLikesBlog(PathMetadata metadata, PathInits inits) {
        this(UserLikesBlog.class, metadata, inits);
    }

    public QUserLikesBlog(Class<? extends UserLikesBlog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.blog = inits.isInitialized("blog") ? new com.github.accountmanagementproject.repository.blog.QBlog(forProperty("blog"), inits.get("blog")) : null;
        this.user = inits.isInitialized("user") ? new com.github.accountmanagementproject.repository.account.users.QMyUser(forProperty("user")) : null;
    }

}

