package com.github.accountmanagementproject.repository.account.users;

import com.github.accountmanagementproject.repository.account.socialIds.QSocialId;
import com.github.accountmanagementproject.repository.account.socialIds.SocialIdPk;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class MyUsersDaoCustomImpl implements MyUsersDaoCustom {
    private final JPAQueryFactory queryFactory;
    private final QMyUser qMyUser = QMyUser.myUser;

    @Override
    public Optional<MyUser> findBySocialIdPk(SocialIdPk socialIdPk) {

        QSocialId qSocialId = QSocialId.socialId;

        MyUser myUser = queryFactory.select(qSocialId.myUser)
                .from(qSocialId)
                .where(qSocialId.socialIdPk.eq(socialIdPk))
                .fetchOne();

        return myUser==null ? Optional.empty() :
                Optional.of(myUser);
    }

//    @Override
//    public Optional<MyUser> findByEmailMinimumInfo(String email) {
//        Tuple tuple = queryFactory.select(qMyUser.userId, qMyUser.email)
//                .from(qMyUser)
//                .where(qMyUser.email.eq(email))
//                .fetchOne();
//
//        MyUser myUser = new MyUser(tuple.get(qMyUser.userId), tuple.get(qMyUser.email));
//        return Optional.of(myUser);
//    }
}
