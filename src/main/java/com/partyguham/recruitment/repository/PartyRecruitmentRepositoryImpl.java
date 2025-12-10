package com.partyguham.recruitment.repository;

import com.partyguham.common.entity.Status;
import com.partyguham.party.dto.party.request.GetPartyRecruitmentsRequestDto;
import com.partyguham.party.entity.QParty;
import com.partyguham.recruitment.dto.request.GetPartyRecruitmentsPersonalizedRequestDto;
import com.partyguham.recruitment.entity.PartyRecruitment;
import com.partyguham.recruitment.entity.QPartyRecruitment;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PartyRecruitmentRepositoryImpl implements PartyRecruitmentCustomRepository {

    @PersistenceContext
    private EntityManager em;
    
    private JPAQueryFactory queryFactory;

    @PostConstruct
    public void init() {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<PartyRecruitment> searchRecruitments(GetPartyRecruitmentsRequestDto request, Pageable pageable) {
        QPartyRecruitment recruitment = QPartyRecruitment.partyRecruitment;
        QParty party = QParty.party;
        
        BooleanBuilder builder = new BooleanBuilder();

        // DELETED 제외
        builder.and(recruitment.status.ne(Status.DELETED));

        // main 필터링
        if (request.getMain() != null && !request.getMain().isBlank()) {
            builder.and(recruitment.position.main.eq(request.getMain()));
        }

        // position 필터링
        if (request.getPosition() != null && !request.getPosition().isEmpty()) {
            builder.and(recruitment.position.id.in(request.getPosition()));
        }

        // partyType 필터링
        if (request.getPartyType() != null && !request.getPartyType().isEmpty()) {
            builder.and(recruitment.party.partyType.id.in(request.getPartyType()));
        }

        // titleSearch 필터링 (파티 제목 검색)
        if (request.getTitleSearch() != null && !request.getTitleSearch().isBlank()) {
            builder.and(recruitment.party.title.containsIgnoreCase(request.getTitleSearch()));
        }

        // 정렬
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(request, recruitment);

        // 조회 (관련 엔티티 fetch join)
        List<PartyRecruitment> results = queryFactory
                .selectFrom(recruitment)
                .leftJoin(recruitment.party, party).fetchJoin()
                .leftJoin(recruitment.party.partyType).fetchJoin()
                .leftJoin(recruitment.position).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifier)
                .fetch();

        // count
        Long total = queryFactory
                .select(recruitment.id.count())
                .from(recruitment)
                .leftJoin(recruitment.party, party)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<PartyRecruitment> searchRecruitmentsPersonalized(GetPartyRecruitmentsPersonalizedRequestDto request,
                                                                 Long positionId,
                                                                 Pageable pageable) {
        QPartyRecruitment recruitment = QPartyRecruitment.partyRecruitment;
        QParty party = QParty.party;
        
        BooleanBuilder builder = new BooleanBuilder();

        // DELETED 제외
        builder.and(recruitment.status.ne(Status.DELETED));

        // position 필터링
        if (positionId != null) {   
            builder.and(recruitment.position.id.eq(positionId));
        }
        
        // 정렬
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(request, recruitment);

        // 조회 (관련 엔티티 fetch join)
        List<PartyRecruitment> results = queryFactory
                .selectFrom(recruitment)
                .leftJoin(recruitment.party, party).fetchJoin()
                .leftJoin(recruitment.position).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifier)
                .fetch();

        // count
        Long total = queryFactory
                .select(recruitment.id.count())
                .from(recruitment)
                .leftJoin(recruitment.party, party)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0L);
    }


    private OrderSpecifier<?> getOrderSpecifier(GetPartyRecruitmentsRequestDto request, QPartyRecruitment recruitment) {
        
        Order order = request.getOrder().equals(Sort.Direction.ASC) ? Order.ASC : Order.DESC;

        return switch (request.getSort()) {
            case "id" -> new OrderSpecifier<>(order, recruitment.id);
            case "createdAt" -> new OrderSpecifier<>(order, recruitment.createdAt);
            case "updatedAt" -> new OrderSpecifier<>(order, recruitment.updatedAt);
            case "content" -> new OrderSpecifier<>(order, recruitment.content);
            case "maxParticipants" -> new OrderSpecifier<>(order, recruitment.maxParticipants);
            case "currentParticipants" -> new OrderSpecifier<>(order, recruitment.currentParticipants);
            case "completed" -> new OrderSpecifier<>(order, recruitment.completed);
            default -> new OrderSpecifier<>(order, recruitment.createdAt);
        };
    }

    private OrderSpecifier<?> getOrderSpecifier(GetPartyRecruitmentsPersonalizedRequestDto request, QPartyRecruitment recruitment) {
        Order order = request.getOrder().equals(Sort.Direction.ASC) ? Order.ASC : Order.DESC;

        return switch (request.getSort()) {
            case "id" -> new OrderSpecifier<>(order, recruitment.id);
            case "createdAt" -> new OrderSpecifier<>(order, recruitment.createdAt);
            case "updatedAt" -> new OrderSpecifier<>(order, recruitment.updatedAt);
            case "content" -> new OrderSpecifier<>(order, recruitment.content);
            case "maxParticipants" -> new OrderSpecifier<>(order, recruitment.maxParticipants);
            case "currentParticipants" -> new OrderSpecifier<>(order, recruitment.currentParticipants);
            case "completed" -> new OrderSpecifier<>(order, recruitment.completed);
            default -> new OrderSpecifier<>(order, recruitment.createdAt);
        };
    }
}

