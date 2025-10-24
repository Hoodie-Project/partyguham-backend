package com.partyguham.party.repository;

import com.partyguham.party.model.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long>, PartyCustomRepository {

}