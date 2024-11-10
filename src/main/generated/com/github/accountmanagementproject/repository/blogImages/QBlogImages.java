package com.github.accountmanagementproject.repository.blogImages;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBlogImages is a Querydsl query type for BlogImages
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBlogImages extends EntityPathBase<BlogImages> {

    private static final long serialVersionUID = -1039077935L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBlogImages blogImages = new QBlogImages("blogImages");

    public final com.github.accountmanagementproject.repository.blog.QBlog blog;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public QBlogImages(String variable) {
        this(BlogImages.class, forVariable(variable), INITS);
    }

    public QBlogImages(Path<? extends BlogImages> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBlogImages(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBlogImages(PathMetadata metadata, PathInits inits) {
        this(BlogImages.class, metadata, inits);
    }

    public QBlogImages(Class<? extends BlogImages> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.blog = inits.isInitialized("blog") ? new com.github.accountmanagementproject.repository.blog.QBlog(forProperty("blog"), inits.get("blog")) : null;
    }

}

