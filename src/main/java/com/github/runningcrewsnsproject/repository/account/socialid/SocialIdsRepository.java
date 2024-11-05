package com.github.runningcrewsnsproject.repository.account.socialid;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialIdsRepository extends JpaRepository<SocialId, Integer>, SocialIdsRepositoryCustom {
}
