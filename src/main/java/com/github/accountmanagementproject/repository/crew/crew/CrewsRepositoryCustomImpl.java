package com.github.accountmanagementproject.repository.crew.crew;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CrewsRepositoryCustomImpl implements CrewsRepositoryCustom {
    private final JPAQueryFactory queryFactory;
}
