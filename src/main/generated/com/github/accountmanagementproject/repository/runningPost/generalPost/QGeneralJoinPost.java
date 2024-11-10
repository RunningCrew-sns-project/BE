package com.github.accountmanagementproject.repository.runningPost.generalPost;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGeneralJoinPost is a Querydsl query type for GeneralJoinPost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGeneralJoinPost extends EntityPathBase<GeneralJoinPost> {

    private static final long serialVersionUID = -1225118816L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGeneralJoinPost generalJoinPost = new QGeneralJoinPost("generalJoinPost");

    public final com.github.accountmanagementproject.repository.account.user.QMyUser author;

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> currentPeople = createNumber("currentPeople", Integer.class);

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<Double> distance = createNumber("distance", Double.class);

    public final ListPath<com.github.accountmanagementproject.repository.runningPost.image.RunJoinPostImage, com.github.accountmanagementproject.repository.runningPost.image.QRunJoinPostImage> generalJoinPostImages = this.<com.github.accountmanagementproject.repository.runningPost.image.RunJoinPostImage, com.github.accountmanagementproject.repository.runningPost.image.QRunJoinPostImage>createList("generalJoinPostImages", com.github.accountmanagementproject.repository.runningPost.image.RunJoinPostImage.class, com.github.accountmanagementproject.repository.runningPost.image.QRunJoinPostImage.class, PathInits.DIRECT2);

    public final NumberPath<Double> inputLatitude = createNumber("inputLatitude", Double.class);

    public final StringPath inputLocation = createString("inputLocation");

    public final NumberPath<Double> inputLongitude = createNumber("inputLongitude", Double.class);

    public final StringPath location = createString("location");

    public final NumberPath<Integer> maximumPeople = createNumber("maximumPeople", Integer.class);

    public final EnumPath<com.github.accountmanagementproject.repository.runningPost.enums.PostType> postType = createEnum("postType", com.github.accountmanagementproject.repository.runningPost.enums.PostType.class);

    public final NumberPath<Long> runId = createNumber("runId", Long.class);

    public final TimePath<java.time.LocalTime> startTime = createTime("startTime", java.time.LocalTime.class);

    public final EnumPath<com.github.accountmanagementproject.repository.runningPost.enums.GeneralRunJoinPostStatus> status = createEnum("status", com.github.accountmanagementproject.repository.runningPost.enums.GeneralRunJoinPostStatus.class);

    public final NumberPath<Double> targetLatitude = createNumber("targetLatitude", Double.class);

    public final StringPath targetLocation = createString("targetLocation");

    public final NumberPath<Double> targetLongitude = createNumber("targetLongitude", Double.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QGeneralJoinPost(String variable) {
        this(GeneralJoinPost.class, forVariable(variable), INITS);
    }

    public QGeneralJoinPost(Path<? extends GeneralJoinPost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGeneralJoinPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGeneralJoinPost(PathMetadata metadata, PathInits inits) {
        this(GeneralJoinPost.class, metadata, inits);
    }

    public QGeneralJoinPost(Class<? extends GeneralJoinPost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new com.github.accountmanagementproject.repository.account.user.QMyUser(forProperty("author")) : null;
    }

}

