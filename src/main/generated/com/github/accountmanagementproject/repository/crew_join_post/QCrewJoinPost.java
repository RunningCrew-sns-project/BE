package com.github.accountmanagementproject.repository.crew_join_post;

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

    private static final long serialVersionUID = 1204134373L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCrewJoinPost crewJoinPost = new QCrewJoinPost("crewJoinPost");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final com.github.accountmanagementproject.repository.crew.crews.QCrew crew;

    public final NumberPath<Double> distance = createNumber("distance", Double.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath inputAddress = createString("inputAddress");

    public final NumberPath<Double> inputLatitude = createNumber("inputLatitude", Double.class);

    public final NumberPath<Double> inputLongitude = createNumber("inputLongitude", Double.class);

    public final NumberPath<Integer> maxCrewNumber = createNumber("maxCrewNumber", Integer.class);

    public final EnumPath<CrewJoinPostStatus> status = createEnum("status", CrewJoinPostStatus.class);

    public final StringPath targetAddress = createString("targetAddress");

    public final NumberPath<Double> targetLatitude = createNumber("targetLatitude", Double.class);

    public final NumberPath<Double> targetLongitude = createNumber("targetLongitude", Double.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final com.github.accountmanagementproject.repository.account.users.QMyUser user;

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
        this.crew = inits.isInitialized("crew") ? new com.github.accountmanagementproject.repository.crew.crews.QCrew(forProperty("crew"), inits.get("crew")) : null;
        this.user = inits.isInitialized("user") ? new com.github.accountmanagementproject.repository.account.users.QMyUser(forProperty("user")) : null;
    }

}

