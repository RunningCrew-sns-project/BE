package com.github.runningcrewsnsproject.repository.account.socialid;

import java.util.Optional;

public interface SocialIdsRepositoryCustom {

    Optional<SocialId> findBySocialIdPkJoinMyUser(SocialIdPk socialIdPk);
}
