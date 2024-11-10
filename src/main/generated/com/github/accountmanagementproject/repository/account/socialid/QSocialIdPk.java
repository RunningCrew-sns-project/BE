package com.github.accountmanagementproject.repository.account.socialid;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSocialIdPk is a Querydsl query type for SocialIdPk
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QSocialIdPk extends BeanPath<SocialIdPk> {

    private static final long serialVersionUID = 653094347L;

    public static final QSocialIdPk socialIdPk = new QSocialIdPk("socialIdPk");

    public final EnumPath<com.github.accountmanagementproject.repository.account.user.myenum.OAuthProvider> provider = createEnum("provider", com.github.accountmanagementproject.repository.account.user.myenum.OAuthProvider.class);

    public final StringPath socialId = createString("socialId");

    public QSocialIdPk(String variable) {
        super(SocialIdPk.class, forVariable(variable));
    }

    public QSocialIdPk(Path<? extends SocialIdPk> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSocialIdPk(PathMetadata metadata) {
        super(SocialIdPk.class, metadata);
    }

}

