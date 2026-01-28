package com.partyguham.domain.party.repository;

import com.partyguham.domain.party.entity.Party;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long>, PartyCustomRepository {

    // N+1 문제 해결 / Lazy Loading 개선 / 지연 로딩으로 인한 성능 저하 해결
    @EntityGraph(attributePaths = {"partyType", "partyUsers", "partyRecruitments"})
    Optional<Party> findById(Long id);

    @Query("select p from Party p " +
            "join fetch p.partyUsers pu " +
            "join fetch pu.user " +
            "where p.id = :partyId")
    Optional<Party> findByIdWithMembers(@Param("partyId") Long partyId);
}