package com.partyguham.domain.party.repository;

import com.partyguham.domain.party.entity.PartyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyTypeRepository extends JpaRepository<PartyType, Long> {
}

