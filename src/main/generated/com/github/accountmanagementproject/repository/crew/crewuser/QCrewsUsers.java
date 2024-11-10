package com.github.accountmanagementproject.repository.crew.crewuser;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCrewsUsers is a Querydsl query type for CrewsUsers
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCrewsUsers extends EntityPathBase<CrewsUsers> {

    private static final long serialVersionUID = 418181736L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCrewsUsers crewsUsers = new QCrewsUsers("crewsUsers");

    public final DateTimePath<java.time.LocalDateTime> applicationDate = createDateTime("applicationDate", java.time.LocalDateTime.class);

    public final NumberPath<Integer> caveat = createNumber("caveat", Integer.class);

    public final QCrewsUsersPk crewsUsersPk;

    public final DateTimePath<java.time.LocalDateTime> joinDate = createDateTime("joinDate", java.time.LocalDateTime.class);

    public final EnumPath<CrewsUsersStatus> status = createEnum("status", CrewsUsersStatus.class);

    public final DateTimePath<java.time.LocalDateTime> withdrawalDate = createDateTime("withdrawalDate", java.time.LocalDateTime.class);

    public QCrewsUsers(String variable) {
        this(CrewsUsers.class, forVariable(variable), INITS);
    }

    public QCrewsUsers(Path<? extends CrewsUsers> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCrewsUsers(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCrewsUsers(PathMetadata metadata, PathInits inits) {
        this(CrewsUsers.class, metadata, inits);
    }

    public QCrewsUsers(Class<? extends CrewsUsers> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.crewsUsersPk = inits.isInitialized("crewsUsersPk") ? new QCrewsUsersPk(forProperty("crewsUsersPk"), inits.get("crewsUsersPk")) : null;
    }

}

