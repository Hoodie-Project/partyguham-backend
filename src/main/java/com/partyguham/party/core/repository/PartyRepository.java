package com.partyguham.party.core.repository;

import com.partyguham.party.core.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long>, PartyCustomRepository {

}