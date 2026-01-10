package com.partyguham.party.repository;

import com.partyguham.common.entity.Status;
import com.partyguham.party.dto.party.request.GetPartiesRequestDto;
import com.partyguham.party.entity.Party;
import com.partyguham.party.entity.QParty;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PartyRepositoryImpl implements PartyCustomRepository {

    @PersistenceContext
    private EntityManager em;
    
    private JPAQueryFactory queryFactory;

    @PostConstruct
    public void init() {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Party> findByTitleKeyword(String keyword) {
        QParty party = QParty.party;

        BooleanBuilder builder = new BooleanBuilder();
        
        if (keyword != null && !keyword.isBlank()) {
            builder.and(party.title.containsIgnoreCase(keyword));
        }

        return queryFactory
                .selectFrom(party)
                .where(builder)
                .orderBy(party.createdAt.desc())
                .fetch();
    }

    @Override
    public Page<Party> findByTitleKeyword(String keyword, Pageable pageable) {
        QParty party = QParty.party;

        BooleanBuilder builder = new BooleanBuilder();
        
        if (keyword != null && !keyword.isBlank()) {
            builder.and(party.title.containsIgnoreCase(keyword));
        }

        // 조회
        List<Party> results = queryFactory
                .selectFrom(party)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(party.createdAt.desc())
                .fetch();

        // count
        Long total = queryFactory
                .select(party.id.count())
                .from(party)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<Party> searchParties(GetPartiesRequestDto request, Pageable pageable) {
        QParty party = QParty.party;
        BooleanBuilder builder = new BooleanBuilder();

        // 레코드 상태가 DELETED 가 아닌 파티만 조회
        builder.and(party.status.ne(Status.DELETED));

        // PartyStatus 필터링
        if (request.getPartyStatus() != null) {
            builder.and(party.partyStatus.eq(request.getPartyStatus()));
        }

        if (request.getTitleSearch() != null && !request.getTitleSearch().isBlank()) {
            builder.and(party.title.containsIgnoreCase(request.getTitleSearch()));
        }

        if (request.getPartyType() != null && !request.getPartyType().isEmpty()) {
            List<Long> partyTypeIds = request.getPartyType().stream()
                    .map(Long::valueOf)
                    .toList();
            builder.and(party.partyType.id.in(partyTypeIds));
        }

        // 조회
        List<Party> results = queryFactory
                .selectFrom(party)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(request))
                .fetch();

        // count
        Long total = queryFactory
                .select(party.id.count())
                .from(party)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0L);
    }

    private OrderSpecifier<?> getOrderSpecifier(GetPartiesRequestDto request) {
        QParty party = QParty.party;
        Direction dir = request.getOrder(); 
        Order order = (dir == Direction.ASC) ? Order.ASC : Order.DESC;

        // 정렬 필드
        String sortField = request.getSort() != null ? request.getSort() : "createdAt";
        
        return switch (sortField) {
            case "createdAt" -> new OrderSpecifier<>(order, party.createdAt);
            default -> new OrderSpecifier<>(order, party.createdAt);
        };
    }
}