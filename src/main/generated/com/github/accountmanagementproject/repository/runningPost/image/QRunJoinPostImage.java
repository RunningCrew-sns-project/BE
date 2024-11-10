package com.github.accountmanagementproject.repository.runningPost.image;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRunJoinPostImage is a Querydsl query type for RunJoinPostImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRunJoinPostImage extends EntityPathBase<RunJoinPostImage> {

    private static final long serialVersionUID = -1035866235L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRunJoinPostImage runJoinPostImage = new QRunJoinPostImage("runJoinPostImage");

    public final com.github.accountmanagementproject.repository.runningPost.crewPost.QCrewJoinPost crewJoinPost;

    public final StringPath fileName = createString("fileName");

    public final com.github.accountmanagementproject.repository.runningPost.generalPost.QGeneralJoinPost generalJoinPost;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final com.github.accountmanagementproject.repository.runningPost.QRunJoinPost runJoinPost;

    public QRunJoinPostImage(String variable) {
        this(RunJoinPostImage.class, forVariable(variable), INITS);
    }

    public QRunJoinPostImage(Path<? extends RunJoinPostImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRunJoinPostImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRunJoinPostImage(PathMetadata metadata, PathInits inits) {
        this(RunJoinPostImage.class, metadata, inits);
    }

    public QRunJoinPostImage(Class<? extends RunJoinPostImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.crewJoinPost = inits.isInitialized("crewJoinPost") ? new com.github.accountmanagementproject.repository.runningPost.crewPost.QCrewJoinPost(forProperty("crewJoinPost"), inits.get("crewJoinPost")) : null;
        this.generalJoinPost = inits.isInitialized("generalJoinPost") ? new com.github.accountmanagementproject.repository.runningPost.generalPost.QGeneralJoinPost(forProperty("generalJoinPost"), inits.get("generalJoinPost")) : null;
        this.runJoinPost = inits.isInitialized("runJoinPost") ? new com.github.accountmanagementproject.repository.runningPost.QRunJoinPost(forProperty("runJoinPost"), inits.get("runJoinPost")) : null;
    }

}

