package com.github.accountmanagementproject.repository.account.user;

import com.github.accountmanagementproject.repository.account.socialid.SocialIdPk;

import java.util.Optional;

public interface MyUsersRepositoryCustom {

    Optional<MyUser> findBySocialIdPk(SocialIdPk socialIdPk);
}
