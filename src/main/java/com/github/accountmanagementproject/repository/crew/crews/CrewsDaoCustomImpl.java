package com.github.accountmanagementproject.repository.crew.crews;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CrewsDaoCustomImpl implements CrewsDaoCustom {
    private final JPAQueryFactory queryFactory;
}
