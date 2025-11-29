package com.partyguham.party.repository;

import com.partyguham.party.entity.PartyAuthority;
import com.partyguham.party.entity.PartyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface PartyUserRepository extends JpaRepository<PartyUser, Long>, PartyUserQueryRepository, PartyUserCustomRepository {
    Optional<PartyUser> findByPartyIdAndUserId(Long partyId, Long userId); //나의 파티 권한 조회

    boolean existsByPartyIdAndUserId(Long partyId, Long userId);

    long countByPartyId(Long partyId);

    Page<PartyUser> findByPartyId(Long partyId, Pageable pageable);

    Page<PartyUser> findByPartyIdAndAuthority(Long partyId, PartyAuthority authority, Pageable pageable);

    Page<PartyUser> findByPartyIdAndUser_NicknameContainingIgnoreCase(Long partyId, String nickname, Pageable pageable);

    Page<PartyUser> findByPartyIdAndAuthorityAndUser_NicknameContainingIgnoreCase(
            Long partyId, PartyAuthority authority, String nickname, Pageable pageable
    );
}
