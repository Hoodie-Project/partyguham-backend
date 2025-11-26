package com.partyguham.party.core.repository;

import com.partyguham.party.core.entity.PartyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyUserRepository extends JpaRepository<PartyUser, Long>, PartyUserCustomRepository {
}
