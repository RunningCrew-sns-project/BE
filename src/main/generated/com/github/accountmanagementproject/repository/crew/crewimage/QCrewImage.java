package com.github.accountmanagementproject.repository.crew.crewimage;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCrewImage is a Querydsl query type for CrewImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCrewImage extends EntityPathBase<CrewImage> {

    private static final long serialVersionUID = -675978794L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCrewImage crewImage = new QCrewImage("crewImage");

    public final com.github.accountmanagementproject.repository.crew.crew.QCrew crew;

    public final NumberPath<Long> crewImageId = createNumber("crewImageId", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public QCrewImage(String variable) {
        this(CrewImage.class, forVariable(variable), INITS);
    }

    public QCrewImage(Path<? extends CrewImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCrewImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCrewImage(PathMetadata metadata, PathInits inits) {
        this(CrewImage.class, metadata, inits);
    }

    public QCrewImage(Class<? extends CrewImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.crew = inits.isInitialized("crew") ? new com.github.accountmanagementproject.repository.crew.crew.QCrew(forProperty("crew"), inits.get("crew")) : null;
    }

}

