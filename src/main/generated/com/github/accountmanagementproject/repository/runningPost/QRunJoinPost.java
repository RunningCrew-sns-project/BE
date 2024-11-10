package com.github.accountmanagementproject.repository.runningPost;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRunJoinPost is a Querydsl query type for RunJoinPost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRunJoinPost extends EntityPathBase<RunJoinPost> {

    private static final long serialVersionUID = -360959351L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRunJoinPost runJoinPost = new QRunJoinPost("runJoinPost");

    public final com.github.accountmanagementproject.repository.account.user.QMyUser author;

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final com.github.accountmanagementproject.repository.crew.crew.QCrew crew;

    public final NumberPath<Integer> crewPostSequence = createNumber("crewPostSequence", Integer.class);

    public final NumberPath<Double> distance = createNumber("distance", Double.class);

    public final NumberPath<Integer> generalPostSequence = createNumber("generalPostSequence", Integer.class);

    public final NumberPath<Double> inputLatitude = createNumber("inputLatitude", Double.class);

    public final StringPath inputLocation = createString("inputLocation");

    public final NumberPath<Double> inputLongitude = createNumber("inputLongitude", Double.class);

    public final ListPath<com.github.accountmanagementproject.repository.runningPost.image.RunJoinPostImage, com.github.accountmanagementproject.repository.runningPost.image.QRunJoinPostImage> joinPostImages = this.<com.github.accountmanagementproject.repository.runningPost.image.RunJoinPostImage, com.github.accountmanagementproject.repository.runningPost.image.QRunJoinPostImage>createList("joinPostImages", com.github.accountmanagementproject.repository.runningPost.image.RunJoinPostImage.class, com.github.accountmanagementproject.repository.runningPost.image.QRunJoinPostImage.class, PathInits.DIRECT2);

    public final NumberPath<Integer> maxParticipants = createNumber("maxParticipants", Integer.class);

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final EnumPath<com.github.accountmanagementproject.repository.runningPost.enums.PostType> postType = createEnum("postType", com.github.accountmanagementproject.repository.runningPost.enums.PostType.class);

    public final EnumPath<com.github.accountmanagementproject.repository.runningPost.enums.RunJoinPostStatus> status = createEnum("status", com.github.accountmanagementproject.repository.runningPost.enums.RunJoinPostStatus.class);

    public final NumberPath<Double> targetLatitude = createNumber("targetLatitude", Double.class);

    public final StringPath targetLocation = createString("targetLocation");

    public final NumberPath<Double> targetLongitude = createNumber("targetLongitude", Double.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QRunJoinPost(String variable) {
        this(RunJoinPost.class, forVariable(variable), INITS);
    }

    public QRunJoinPost(Path<? extends RunJoinPost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRunJoinPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRunJoinPost(PathMetadata metadata, PathInits inits) {
        this(RunJoinPost.class, metadata, inits);
    }

    public QRunJoinPost(Class<? extends RunJoinPost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new com.github.accountmanagementproject.repository.account.user.QMyUser(forProperty("author")) : null;
        this.crew = inits.isInitialized("crew") ? new com.github.accountmanagementproject.repository.crew.crew.QCrew(forProperty("crew"), inits.get("crew")) : null;
    }

}

