package com.github.runningcrewsnsproject.repository.account.user;

import com.github.runningcrewsnsproject.repository.account.socialid.SocialIdPk;

import java.util.Optional;

public interface MyUsersRepositoryCustom {

    Optional<MyUser> findBySocialIdPk(SocialIdPk socialIdPk);
}
