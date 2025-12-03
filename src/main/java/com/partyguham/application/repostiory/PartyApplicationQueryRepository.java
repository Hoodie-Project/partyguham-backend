package com.partyguham.application.repostiory;

import com.partyguham.application.dto.req.PartyApplicantSearchRequestDto;
import com.partyguham.application.entity.PartyApplication;
import com.partyguham.application.entity.PartyApplicationStatus;
import com.partyguham.common.entity.Status;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.partyguham.application.entity.QPartyApplication.partyApplication;
import static com.partyguham.recruitment.entity.QPartyRecruitment.partyRecruitment;
import static com.partyguham.party.entity.QPartyUser.partyUser;
import static com.partyguham.user.account.entity.QUser.user;
import static com.partyguham.user.profile.entity.QUserProfile.userProfile;

@Repository
@RequiredArgsConstructor
public class PartyApplicationQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 파티 모집 지원자 목록 조회 (상태/정렬/페이징)
     */
    public Page<PartyApplication> searchApplicants(Long partyId,
                                                   Long recruitmentId,
                                                   PartyApplicantSearchRequestDto request,
                                                   Pageable pageable) {

        // 정렬 방향
        Order direction = "ASC".equalsIgnoreCase(request.getOrder())
                ? Order.ASC
                : Order.DESC;

        OrderSpecifier<?> orderSpec = new OrderSpecifier<>(
                direction,
                partyApplication.createdAt
        );

        // 상태 필터 (optional)
        PartyApplicationStatus statusFilter = null;
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            statusFilter = PartyApplicationStatus.valueOf(request.getStatus().toUpperCase());
        }

        // 본문 쿼리
        JPAQuery<PartyApplication> baseQuery = queryFactory
                .selectFrom(partyApplication)
                .join(partyApplication.partyRecruitment, partyRecruitment)
                .join(partyApplication.partyUser, partyUser).fetchJoin()
                .join(partyUser.user, user).fetchJoin()
                .leftJoin(user.profile, userProfile).fetchJoin()
                .where(
                        partyRecruitment.id.eq(recruitmentId),
                        partyRecruitment.party.id.eq(partyId),
                        partyApplication.status.ne(Status.DELETED),
                        statusFilter != null
                                ? partyApplication.applicationStatus.eq(statusFilter)
                                : null
                )
                .orderBy(orderSpec);

        // 페이징 + 결과
        List<PartyApplication> content = baseQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(partyApplication.count())
                .from(partyApplication)
                .join(partyApplication.partyRecruitment, partyRecruitment)
                .where(
                        partyRecruitment.id.eq(recruitmentId),
                        partyRecruitment.party.id.eq(partyId),
                        partyApplication.status.ne(Status.DELETED),
                        statusFilter != null
                                ? partyApplication.applicationStatus.eq(statusFilter)
                                : null
                );

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                countQuery::fetchOne
        );
    }
}