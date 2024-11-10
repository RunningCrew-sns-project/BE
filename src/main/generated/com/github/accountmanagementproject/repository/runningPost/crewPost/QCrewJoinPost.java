package com.github.accountmanagementproject.repository.runningPost.crewPost;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCrewJoinPost is a Querydsl query type for CrewJoinPost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCrewJoinPost extends EntityPathBase<CrewJoinPost> {

    private static final long serialVersionUID = -1497495092L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCrewJoinPost crewJoinPost = new QCrewJoinPost("crewJoinPost");

    public final com.github.accountmanagementproject.repository.account.user.QMyUser author;

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final com.github.accountmanagementproject.repository.crew.crew.QCrew crew;

    public final ListPath<com.github.accountmanagementproject.repository.runningPost.image.RunJoinPostImage, com.github.accountmanagementproject.repository.runningPost.image.QRunJoinPostImage> crewJoinPostImages = this.<com.github.accountmanagementproject.repository.runningPost.image.RunJoinPostImage, com.github.accountmanagementproject.repository.runningPost.image.QRunJoinPostImage>createList("crewJoinPostImages", com.github.accountmanagementproject.repository.runningPost.image.RunJoinPostImage.class, com.github.accountmanagementproject.repository.runningPost.image.QRunJoinPostImage.class, PathInits.DIRECT2);

    public final NumberPath<Integer> currentPeople = createNumber("currentPeople", Integer.class);

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<Double> distance = createNumber("distance", Double.class);

    public final NumberPath<Double> inputLatitude = createNumber("inputLatitude", Double.class);

    public final StringPath inputLocation = createString("inputLocation");

    public final NumberPath<Double> inputLongitude = createNumber("inputLongitude", Double.class);

    public final StringPath location = createString("location");

    public final NumberPath<Integer> maximumPeople = createNumber("maximumPeople", Integer.class);

    public final EnumPath<com.github.accountmanagementproject.repository.runningPost.enums.PostType> postType = createEnum("postType", com.github.accountmanagementproject.repository.runningPost.enums.PostType.class);

    public final NumberPath<Long> runId = createNumber("runId", Long.class);

    public final TimePath<java.time.LocalTime> startTime = createTime("startTime", java.time.LocalTime.class);

    public final EnumPath<com.github.accountmanagementproject.repository.runningPost.enums.CrewRunJoinPostStatus> status = createEnum("status", com.github.accountmanagementproject.repository.runningPost.enums.CrewRunJoinPostStatus.class);

    public final NumberPath<Double> targetLatitude = createNumber("targetLatitude", Double.class);

    public final StringPath targetLocation = createString("targetLocation");

    public final NumberPath<Double> targetLongitude = createNumber("targetLongitude", Double.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QCrewJoinPost(String variable) {
        this(CrewJoinPost.class, forVariable(variable), INITS);
    }

    public QCrewJoinPost(Path<? extends CrewJoinPost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCrewJoinPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCrewJoinPost(PathMetadata metadata, PathInits inits) {
        this(CrewJoinPost.class, metadata, inits);
    }

    public QCrewJoinPost(Class<? extends CrewJoinPost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new com.github.accountmanagementproject.repository.account.user.QMyUser(forProperty("author")) : null;
        this.crew = inits.isInitialized("crew") ? new com.github.accountmanagementproject.repository.crew.crew.QCrew(forProperty("crew"), inits.get("crew")) : null;
    }

}

