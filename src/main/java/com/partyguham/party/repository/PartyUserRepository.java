package com.partyguham.party.repository;

import com.partyguham.party.entity.PartyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartyUserRepository extends JpaRepository<PartyUser, Long>, PartyUserCustomRepository {
    Optional<PartyUser> findByPartyIdAndUserId(Long partyId, Long userId); //나의 파티 권한 조회
}
