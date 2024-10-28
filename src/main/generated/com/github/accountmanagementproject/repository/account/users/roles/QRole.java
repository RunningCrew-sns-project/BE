package com.github.accountmanagementproject.repository.account.users.roles;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRole is a Querydsl query type for Role
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRole extends EntityPathBase<Role> {

    private static final long serialVersionUID = 735571019L;

    public static final QRole role = new QRole("role");

    public final SetPath<com.github.accountmanagementproject.repository.account.users.MyUser, com.github.accountmanagementproject.repository.account.users.QMyUser> myUsers = this.<com.github.accountmanagementproject.repository.account.users.MyUser, com.github.accountmanagementproject.repository.account.users.QMyUser>createSet("myUsers", com.github.accountmanagementproject.repository.account.users.MyUser.class, com.github.accountmanagementproject.repository.account.users.QMyUser.class, PathInits.DIRECT2);

    public final EnumPath<com.github.accountmanagementproject.repository.account.users.enums.RolesEnum> name = createEnum("name", com.github.accountmanagementproject.repository.account.users.enums.RolesEnum.class);

    public final NumberPath<Integer> rolesId = createNumber("rolesId", Integer.class);

    public QRole(String variable) {
        super(Role.class, forVariable(variable));
    }

    public QRole(Path<? extends Role> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRole(PathMetadata metadata) {
        super(Role.class, metadata);
    }

}

