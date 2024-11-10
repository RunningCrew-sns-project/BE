package com.github.accountmanagementproject.repository.crew.crew;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCrew is a Querydsl query type for Crew
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCrew extends EntityPathBase<Crew> {

    private static final long serialVersionUID = 1641903038L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCrew crew = new QCrew("crew");

    public final StringPath activityRegion = createString("activityRegion");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final ListPath<com.github.accountmanagementproject.repository.crew.crewattachment.CrewAttachment, com.github.accountmanagementproject.repository.crew.crewattachment.QCrewAttachment> crewAttachments = this.<com.github.accountmanagementproject.repository.crew.crewattachment.CrewAttachment, com.github.accountmanagementproject.repository.crew.crewattachment.QCrewAttachment>createList("crewAttachments", com.github.accountmanagementproject.repository.crew.crewattachment.CrewAttachment.class, com.github.accountmanagementproject.repository.crew.crewattachment.QCrewAttachment.class, PathInits.DIRECT2);

    public final NumberPath<Long> crewId = createNumber("crewId", Long.class);

    public final ListPath<com.github.accountmanagementproject.repository.crew.crewimage.CrewImage, com.github.accountmanagementproject.repository.crew.crewimage.QCrewImage> crewImages = this.<com.github.accountmanagementproject.repository.crew.crewimage.CrewImage, com.github.accountmanagementproject.repository.crew.crewimage.QCrewImage>createList("crewImages", com.github.accountmanagementproject.repository.crew.crewimage.CrewImage.class, com.github.accountmanagementproject.repository.crew.crewimage.QCrewImage.class, PathInits.DIRECT2);

    public final StringPath crewIntroduction = createString("crewIntroduction");

    public final com.github.accountmanagementproject.repository.account.user.QMyUser crewMaster;

    public final StringPath crewName = createString("crewName");

    public final ListPath<com.github.accountmanagementproject.repository.account.user.MyUser, com.github.accountmanagementproject.repository.account.user.QMyUser> crewUsers = this.<com.github.accountmanagementproject.repository.account.user.MyUser, com.github.accountmanagementproject.repository.account.user.QMyUser>createList("crewUsers", com.github.accountmanagementproject.repository.account.user.MyUser.class, com.github.accountmanagementproject.repository.account.user.QMyUser.class, PathInits.DIRECT2);

    public final NumberPath<Integer> maxCapacity = createNumber("maxCapacity", Integer.class);

    public QCrew(String variable) {
        this(Crew.class, forVariable(variable), INITS);
    }

    public QCrew(Path<? extends Crew> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCrew(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCrew(PathMetadata metadata, PathInits inits) {
        this(Crew.class, metadata, inits);
    }

    public QCrew(Class<? extends Crew> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.crewMaster = inits.isInitialized("crewMaster") ? new com.github.accountmanagementproject.repository.account.user.QMyUser(forProperty("crewMaster")) : null;
    }

}

