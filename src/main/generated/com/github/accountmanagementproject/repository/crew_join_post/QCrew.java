package com.github.accountmanagementproject.repository.crew_join_post;

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

    private static final long serialVersionUID = -1247673381L;

    public static final QCrew crew = new QCrew("crew");

    public final StringPath crewIntroduction = createString("crewIntroduction");

    public final ListPath<CrewJoinPost, QCrewJoinPost> crewJoinPostList = this.<CrewJoinPost, QCrewJoinPost>createList("crewJoinPostList", CrewJoinPost.class, QCrewJoinPost.class, PathInits.DIRECT2);

    public final StringPath crewMaster = createString("crewMaster");

    public final StringPath crewName = createString("crewName");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final SetPath<com.github.accountmanagementproject.repository.account.users.MyUser, com.github.accountmanagementproject.repository.account.users.QMyUser> users = this.<com.github.accountmanagementproject.repository.account.users.MyUser, com.github.accountmanagementproject.repository.account.users.QMyUser>createSet("users", com.github.accountmanagementproject.repository.account.users.MyUser.class, com.github.accountmanagementproject.repository.account.users.QMyUser.class, PathInits.DIRECT2);

    public QCrew(String variable) {
        super(Crew.class, forVariable(variable));
    }

    public QCrew(Path<? extends Crew> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCrew(PathMetadata metadata) {
        super(Crew.class, metadata);
    }

}

