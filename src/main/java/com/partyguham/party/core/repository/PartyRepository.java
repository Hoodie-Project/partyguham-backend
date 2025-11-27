package com.partyguham.party.core.repository;

import com.partyguham.party.core.entity.Party;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long>, PartyCustomRepository {

    // N+1 문제 해결 / Lazy Loading 개선 / 지연 로딩으로 인한 성능 저하 해결
    @EntityGraph(attributePaths = {"partyType", "partyUsers", "partyRecruitments"})
    Optional<Party> findById(Long id);
}