package com.github.accountmanagementproject.repository.crew.crewattachment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCrewAttachment is a Querydsl query type for CrewAttachment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCrewAttachment extends EntityPathBase<CrewAttachment> {

    private static final long serialVersionUID = -1017112258L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCrewAttachment crewAttachment = new QCrewAttachment("crewAttachment");

    public final com.github.accountmanagementproject.repository.crew.crew.QCrew crew;

    public final NumberPath<Long> crewAttachmentId = createNumber("crewAttachmentId", Long.class);

    public final StringPath fileName = createString("fileName");

    public final StringPath fileUrl = createString("fileUrl");

    public QCrewAttachment(String variable) {
        this(CrewAttachment.class, forVariable(variable), INITS);
    }

    public QCrewAttachment(Path<? extends CrewAttachment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCrewAttachment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCrewAttachment(PathMetadata metadata, PathInits inits) {
        this(CrewAttachment.class, metadata, inits);
    }

    public QCrewAttachment(Class<? extends CrewAttachment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.crew = inits.isInitialized("crew") ? new com.github.accountmanagementproject.repository.crew.crew.QCrew(forProperty("crew"), inits.get("crew")) : null;
    }

}

