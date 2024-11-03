package com.github.accountmanagementproject.repository.account.socialid;

import java.util.Optional;

public interface SocialIdsRepositoryCustom {

    Optional<SocialId> findBySocialIdPkJoinMyUser(SocialIdPk socialIdPk);
}