package com.partyguham.party.recruitment.repository;

import com.partyguham.party.recruitment.entity.PartyRecruitment;
import com.partyguham.party.recruitment.entity.QPartyRecruitment;
import com.querydsl.core.BooleanBuilder;
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
public class PartyRecruitmentRepositoryImpl implements PartyRecruitmentCustomRepository {

    @PersistenceContext
    private EntityManager em;
    
    private JPAQueryFactory queryFactory;

    @PostConstruct
    public void init() {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<PartyRecruitment> findByTitleKeyword(String keyword, Pageable pageable) {
        QPartyRecruitment partyRecruitment = QPartyRecruitment.partyRecruitment;
        BooleanBuilder builder = new BooleanBuilder();
        
        if (keyword != null && !keyword.isBlank()) {
            builder.and(partyRecruitment.title.containsIgnoreCase(keyword));
        }

        // 조회 (관련 엔티티 fetch join)
        List<PartyRecruitment> results = queryFactory
                .selectFrom(partyRecruitment)
                .leftJoin(partyRecruitment.party).fetchJoin()
                .leftJoin(partyRecruitment.party.partyType).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(partyRecruitment.createdAt.desc())
                .fetch();

        // count
        Long total = queryFactory
                .select(Expressions.ONE.count())
                .from(partyRecruitment)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0L);
    }
}

