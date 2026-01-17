package com.partyguham.user.my.repository;

import com.partyguham.application.entity.PartyApplication;
import com.partyguham.application.entity.PartyApplicationStatus;
import com.partyguham.common.entity.Status;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.partyguham.application.entity.QPartyApplication.partyApplication;
import static com.partyguham.recruitment.entity.QPartyRecruitment.partyRecruitment;
import static com.partyguham.catalog.entity.QPosition.position;
import static com.partyguham.party.entity.QParty.party;
import static com.partyguham.party.entity.QPartyType.partyType;

@Repository
@RequiredArgsConstructor
public class MyPartyApplicationQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 내가 지원한 모집 목록 조회
     */
    public Page<PartyApplication> searchMyApplications(Long userId,
                                                       List<PartyApplicationStatus> statusFilters, // List로 변경
                                                       Pageable pageable,
                                                       Order orderDir) {

        OrderSpecifier<?> orderSpec = new OrderSpecifier<>(
                orderDir,
                partyApplication.createdAt
        );

        // 본문 쿼리
        JPAQuery<PartyApplication> baseQuery = queryFactory
                .selectFrom(partyApplication)
                .join(partyApplication.partyRecruitment, partyRecruitment).fetchJoin()
                .join(partyRecruitment.position, position).fetchJoin()
                .join(partyRecruitment.party, party).fetchJoin()
                .join(party.partyType, partyType).fetchJoin()
                .where(
                        partyApplication.user.id.eq(userId),
                        partyApplication.status.ne(Status.DELETED),
                        statusIn(statusFilters) // 메서드 추출로 깔끔하게 정리
                )
                .orderBy(orderSpec);

        List<PartyApplication> content = baseQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(partyApplication.count())
                .from(partyApplication)
                .where(
                        partyApplication.user.id.eq(userId),
                        partyApplication.status.ne(Status.DELETED),
                        statusIn(statusFilters) // 동일하게 적용
                );

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                countQuery::fetchOne
        );
    }

    // 동적 쿼리를 위한 도우미 메서드
    private BooleanExpression statusIn(List<PartyApplicationStatus> statusFilters) {
        // 리스트가 null이거나 비어있으면 조건문을 생성하지 않음 (전체 조회)
        if (statusFilters == null || statusFilters.isEmpty()) {
            return null;
        }
        return partyApplication.applicationStatus.in(statusFilters);
    }
}