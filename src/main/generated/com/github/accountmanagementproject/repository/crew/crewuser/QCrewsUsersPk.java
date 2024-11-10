package com.github.accountmanagementproject.repository.crew.crewuser;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCrewsUsersPk is a Querydsl query type for CrewsUsersPk
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QCrewsUsersPk extends BeanPath<CrewsUsersPk> {

    private static final long serialVersionUID = -1854274941L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCrewsUsersPk crewsUsersPk = new QCrewsUsersPk("crewsUsersPk");

    public final com.github.accountmanagementproject.repository.crew.crew.QCrew crew;

    public final com.github.accountmanagementproject.repository.account.user.QMyUser user;

    public QCrewsUsersPk(String variable) {
        this(CrewsUsersPk.class, forVariable(variable), INITS);
    }

    public QCrewsUsersPk(Path<? extends CrewsUsersPk> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCrewsUsersPk(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCrewsUsersPk(PathMetadata metadata, PathInits inits) {
        this(CrewsUsersPk.class, metadata, inits);
    }

    public QCrewsUsersPk(Class<? extends CrewsUsersPk> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.crew = inits.isInitialized("crew") ? new com.github.accountmanagementproject.repository.crew.crew.QCrew(forProperty("crew"), inits.get("crew")) : null;
        this.user = inits.isInitialized("user") ? new com.github.accountmanagementproject.repository.account.user.QMyUser(forProperty("user")) : null;
    }

}

