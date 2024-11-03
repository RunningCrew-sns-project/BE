package com.github.accountmanagementproject.repository.blogComment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBlogComment is a Querydsl query type for BlogComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBlogComment extends EntityPathBase<BlogComment> {

    private static final long serialVersionUID = 1833761455L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBlogComment blogComment = new QBlogComment("blogComment");

    public final com.github.accountmanagementproject.repository.blog.QBlog blog;

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final com.github.accountmanagementproject.repository.account.users.QMyUser user;

    public QBlogComment(String variable) {
        this(BlogComment.class, forVariable(variable), INITS);
    }

    public QBlogComment(Path<? extends BlogComment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBlogComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBlogComment(PathMetadata metadata, PathInits inits) {
        this(BlogComment.class, metadata, inits);
    }

    public QBlogComment(Class<? extends BlogComment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.blog = inits.isInitialized("blog") ? new com.github.accountmanagementproject.repository.blog.QBlog(forProperty("blog"), inits.get("blog")) : null;
        this.user = inits.isInitialized("user") ? new com.github.accountmanagementproject.repository.account.users.QMyUser(forProperty("user")) : null;
    }

}

