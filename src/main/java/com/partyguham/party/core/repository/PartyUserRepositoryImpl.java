package com.partyguham.party.core.repository;

import com.partyguham.party.core.entity.PartyUser;
import com.partyguham.party.core.entity.QPartyUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PartyUserRepositoryImpl implements PartyUserCustomRepository {

    @PersistenceContext
    private EntityManager em;
    
    private JPAQueryFactory queryFactory;

    @PostConstruct
    public void init() {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<PartyUser> findPartyUsers(
            Long partyId,
            String main,
            String nickname,
            String sort,
            String order,
            Pageable pageable
    ) {
        QPartyUser partyUser = QPartyUser.partyUser;
        BooleanBuilder builder = new BooleanBuilder();

        // 파티 ID 필터링 (필수)
        if (partyId != null) {
            builder.and(partyUser.party.id.eq(partyId));
        }

        // 직군(main) 필터링
        if (main != null && !main.isBlank()) {
            builder.and(partyUser.position.main.eq(main));
        }

        // 닉네임 검색
        if (nickname != null && !nickname.isBlank()) {
            builder.and(partyUser.user.nickname.containsIgnoreCase(nickname));
        }

        // 조회 (관련 엔티티 fetch join)
        List<PartyUser> results = queryFactory
                .selectFrom(partyUser)
                .leftJoin(partyUser.user).fetchJoin()
                .leftJoin(partyUser.user.profile).fetchJoin()
                .leftJoin(partyUser.position).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(partyUser, sort, order))
                .fetch();

        // count
        Long total = queryFactory
                .select(Expressions.ONE.count())
                .from(partyUser)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0L);
    }

    private OrderSpecifier<?> getOrderSpecifier(QPartyUser partyUser, String sort, String order) {
        // 기본 정렬: createdAt ASC
        Order direction = Order.ASC;
        
        if (order != null) {
            direction = order.equalsIgnoreCase("DESC") ? Order.DESC : Order.ASC;
        }

        // 정렬 필드
        String sortField = sort != null ? sort : "createdAt";
        
        return switch (sortField) {
            case "createdAt" -> new OrderSpecifier<>(direction, partyUser.createdAt);
            default -> new OrderSpecifier<>(direction, partyUser.createdAt);
        };
    }
}

