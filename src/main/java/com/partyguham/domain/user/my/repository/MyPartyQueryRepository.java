package com.partyguham.domain.user.my.repository;

import com.partyguham.global.entity.Status;
import com.partyguham.domain.party.entity.PartyStatus;
import com.partyguham.domain.party.entity.PartyUser;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.partyguham.domain.party.entity.QParty.party;
import static com.partyguham.domain.party.entity.QPartyType.partyType;
import static com.partyguham.domain.party.entity.QPartyUser.partyUser;
import static com.partyguham.domain.catalog.entity.QPosition.position;

@Repository
@RequiredArgsConstructor
public class MyPartyQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 로그인한 유저가 속한 파티 목록 조회
     */
    public Page<PartyUser> searchMyParties(Long userId,
                                           PartyStatus partyStatusFilter,
                                           Pageable pageable,
                                           Order direction) {

        OrderSpecifier<?> orderSpec =
                new OrderSpecifier<>(direction, partyUser.createdAt);

        // 본문 쿼리
        JPAQuery<PartyUser> baseQuery = queryFactory
                .selectFrom(partyUser)
                .join(partyUser.party, party).fetchJoin()
                .leftJoin(party.partyType, partyType).fetchJoin()
                .leftJoin(partyUser.position, position).fetchJoin()
                .where(
                        partyUser.user.id.eq(userId),
                        partyUser.status.ne(Status.DELETED),
                        partyStatusFilter != null ? party.partyStatus.eq(partyStatusFilter) : null
                )
                .orderBy(orderSpec);

        List<PartyUser> content = baseQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(partyUser.count())
                .from(partyUser)
                .join(partyUser.party, party)
                .where(
                        partyUser.user.id.eq(userId),
                        partyUser.status.ne(Status.DELETED),
                        partyStatusFilter != null ? party.partyStatus.eq(partyStatusFilter) : null
                );

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                countQuery::fetchOne
        );
    }
}