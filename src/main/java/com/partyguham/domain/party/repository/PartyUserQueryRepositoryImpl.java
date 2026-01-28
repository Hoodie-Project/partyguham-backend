package com.partyguham.domain.party.repository;

import com.partyguham.domain.party.dto.partyAdmin.request.GetAdminPartyUsersRequestDto;
import com.partyguham.domain.party.entity.PartyUser;
import com.partyguham.domain.party.entity.PartyAuthority;
import com.partyguham.global.entity.Status;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.partyguham.domain.party.entity.QParty.party;
import static com.partyguham.domain.party.entity.QPartyType.partyType;
import static com.partyguham.domain.party.entity.QPartyUser.partyUser;
import static com.partyguham.domain.user.account.entity.QUser.user;
import static com.partyguham.domain.catalog.entity.QPosition.position;
import static com.partyguham.domain.user.profile.entity.QUserProfile.userProfile;

@Repository
@RequiredArgsConstructor
public class PartyUserQueryRepositoryImpl implements PartyUserQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public long countAllByPartyIdNotDeleted(Long partyId) {
        // 이 파티에 삭제되지 않은 파티원이 몇 명인지 (전체 인원 수)
        Long count = queryFactory
                .select(partyUser.count())
                .from(partyUser)
                .where(
                        partyUser.party.id.eq(partyId),
                        partyUser.status.ne(Status.DELETED)
                )
                .fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    public Page<PartyUser> searchAdminPartyUsers(Long partyId,
                                                 GetAdminPartyUsersRequestDto req,
                                                 Pageable pageable) {

        BooleanBuilder where = buildWhere(partyId, req);

        // 1) 내용 조회 (join + fetchJoin + 페이징)
        List<PartyUser> content = queryFactory
                .selectFrom(partyUser)
                .join(partyUser.user, user).fetchJoin()
                .leftJoin(user.profile, userProfile).fetchJoin()
                .leftJoin(partyUser.position, position).fetchJoin()
                .where(where)
                .orderBy(partyUser.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 2) 카운트 조회 (페이징 계산용)
        Long total = queryFactory
                .select(partyUser.count())
                .from(partyUser)
                .join(partyUser.user, user)
                .leftJoin(partyUser.position, position)
                .where(where)
                .fetchOne();

        long totalCount = total != null ? total : 0L;

        return new PageImpl<>(content, pageable, totalCount);
    }

    /**
     * 필터 조건(authority, nickname, main 등)을 한 곳에서 조립
     */
    private BooleanBuilder buildWhere(Long partyId, GetAdminPartyUsersRequestDto req) {
        BooleanBuilder where = new BooleanBuilder()
                .and(partyUser.party.id.eq(partyId))
                .and(partyUser.status.ne(Status.DELETED));

        PartyAuthority authority = req.getAuthority();
        String nickname = req.getNickname();
        String main = req.getMain();

        if (authority != null) {
            where.and(partyUser.authority.eq(authority));
        }
        if (nickname != null && !nickname.isBlank()) {
            where.and(user.nickname.containsIgnoreCase(nickname));
        }
        if (main != null && !main.isBlank()) {
            where.and(position.main.eq(main));
        }

        return where;
    }

    @Override
    public Page<PartyUser> searchAdminPartyUsers(Long partyId,
                                                 String main,
                                                 PartyAuthority authority,
                                                 String nickname,
                                                 String sort,
                                                 String order,
                                                 Pageable pageable) {

        // 기본 where 조건
        BooleanExpression baseCondition =
                partyUser.party.id.eq(partyId)
                        .and(partyUser.status.ne(Status.DELETED));

        BooleanExpression authorityCond = authorityEq(authority);
        BooleanExpression nicknameCond = nicknameContains(nickname);
        BooleanExpression mainCond = mainEq(main);

        // 조회 쿼리
        JPAQuery<PartyUser> contentQuery = queryFactory
                .selectFrom(partyUser)
                .leftJoin(partyUser.user, user).fetchJoin()
                .leftJoin(user.profile, userProfile).fetchJoin()
                .leftJoin(partyUser.position, position).fetchJoin()
                .where(
                        baseCondition,
                        authorityCond,
                        nicknameCond,
                        mainCond
                );

        // 정렬 적용
        OrderSpecifier<?> orderSpecifier = toOrderSpecifier(sort, order);
        if (orderSpecifier != null) {
            contentQuery.orderBy(orderSpecifier);
        }

        // 페이징 적용
        List<PartyUser> content = contentQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리 (fetchJoin 제거)
        JPAQuery<Long> countQuery = queryFactory
                .select(partyUser.count())
                .from(partyUser)
                .leftJoin(partyUser.user, user)
                .leftJoin(partyUser.position, position)
                .where(
                        baseCondition,
                        authorityCond,
                        nicknameCond,
                        mainCond
                );

        long total = countQuery.fetchOne() != null ? countQuery.fetchOne() : 0L;

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression authorityEq(PartyAuthority authority) {
        return authority != null ? partyUser.authority.eq(authority) : null;
    }

    private BooleanExpression nicknameContains(String nickname) {
        return (nickname != null && !nickname.isBlank())
                ? user.nickname.containsIgnoreCase(nickname)
                : null;
    }

    private BooleanExpression mainEq(String main) {
        return (main != null && !main.isBlank())
                ? position.main.eq(main)
                : null;
    }

    private OrderSpecifier<?> toOrderSpecifier(String sort, String order) {
        Order direction = "asc".equalsIgnoreCase(order) ? Order.ASC : Order.DESC;

        String safeSort = (sort != null) ? sort : "id";

        return switch (safeSort) {
            case "createdAt" -> new OrderSpecifier<>(direction, partyUser.createdAt);
            case "updatedAt" -> new OrderSpecifier<>(direction, partyUser.updatedAt);
            default -> new OrderSpecifier<>(direction, partyUser.id);
        };
    }

    @Override
    public List<PartyUser> findByUserNickname(String nickname) {

        return queryFactory
                .selectFrom(partyUser)
                .join(partyUser.user, user).fetchJoin()
                .join(partyUser.party, party).fetchJoin()
                .join(partyUser.position, position).fetchJoin()
                .join(party.partyType, partyType).fetchJoin()
                .where(user.nickname.eq(nickname))
                .fetch();
    }
}