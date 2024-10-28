package com.github.accountmanagementproject.repository.blog;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBlog is a Querydsl query type for Blog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBlog extends EntityPathBase<Blog> {

    private static final long serialVersionUID = -662504879L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBlog blog = new QBlog("blog");

    public final StringPath content = createString("content");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath image = createString("image");

    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public final StringPath record = createString("record");

    public final StringPath title = createString("title");

    public final com.github.accountmanagementproject.repository.account.users.QMyUser user;

    public QBlog(String variable) {
        this(Blog.class, forVariable(variable), INITS);
    }

    public QBlog(Path<? extends Blog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBlog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBlog(PathMetadata metadata, PathInits inits) {
        this(Blog.class, metadata, inits);
    }

    public QBlog(Class<? extends Blog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.github.accountmanagementproject.repository.account.users.QMyUser(forProperty("user")) : null;
    }

}

