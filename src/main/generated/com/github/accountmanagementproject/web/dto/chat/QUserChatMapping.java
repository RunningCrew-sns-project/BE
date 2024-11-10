package com.github.accountmanagementproject.web.dto.chat;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserChatMapping is a Querydsl query type for UserChatMapping
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserChatMapping extends EntityPathBase<UserChatMapping> {

    private static final long serialVersionUID = 1551500101L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserChatMapping userChatMapping = new QUserChatMapping("userChatMapping");

    public final QChatRoom chatRoom;

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final com.github.accountmanagementproject.repository.account.user.QMyUser user;

    public QUserChatMapping(String variable) {
        this(UserChatMapping.class, forVariable(variable), INITS);
    }

    public QUserChatMapping(Path<? extends UserChatMapping> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserChatMapping(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserChatMapping(PathMetadata metadata, PathInits inits) {
        this(UserChatMapping.class, metadata, inits);
    }

    public QUserChatMapping(Class<? extends UserChatMapping> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.chatRoom = inits.isInitialized("chatRoom") ? new QChatRoom(forProperty("chatRoom")) : null;
        this.user = inits.isInitialized("user") ? new com.github.accountmanagementproject.repository.account.user.QMyUser(forProperty("user")) : null;
    }

}

