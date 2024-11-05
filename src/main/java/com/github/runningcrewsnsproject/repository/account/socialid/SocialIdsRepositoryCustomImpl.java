package com.github.runningcrewsnsproject.repository.account.socialid;

import com.github.runningcrewsnsproject.repository.account.user.QMyUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
@RequiredArgsConstructor
public class SocialIdsRepositoryCustomImpl implements SocialIdsRepositoryCustom {
    private final JPAQueryFactory queryFactory;


    @Override
    public Optional<SocialId> findBySocialIdPkJoinMyUser(SocialIdPk socialIdPk) {
        QSocialId qSocialId = QSocialId.socialId;

        SocialId socialId = queryFactory
                .selectFrom(qSocialId)
                .join(qSocialId.myUser, QMyUser.myUser).fetchJoin()
                .where(qSocialId.socialIdPk.eq(socialIdPk))
                .fetchOne();

        return socialId == null ? Optional.empty() : Optional.of(socialId);
    }
}
