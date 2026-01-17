package com.partyguham.party.repository;

import com.partyguham.common.entity.Status;
import com.partyguham.party.entity.PartyAuthority;
import com.partyguham.party.entity.PartyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyUserRepository extends JpaRepository<PartyUser, Long>, PartyUserQueryRepository, PartyUserCustomRepository {

    // PK + 상태 조회
    Optional<PartyUser> findByIdAndStatus(Long id, Status status);

    Optional<PartyUser> findByPartyIdAndUserIdAndStatus(Long partyId, Long userId, Status status);

    Optional<PartyUser> findByPartyIdAndAuthority(Long partyId, PartyAuthority authority);

    List<PartyUser> findByPartyIdAndIdInAndStatus(Long partyId,
                                                      List<Long> ids,
                                                      Status status);

    List<PartyUser> findByPartyIdAndStatus(Long partyId, Status status);

    /**
     * 특정 파티에 특정 유저가 속해있는지 확인하는 메서드
     * (단, 삭제된 멤버는 제외)
     *
     * - party.id = :partyId
     * - user.id = :userId
     * - status != :excludedStatus
     *
     * → 해당 유저가 파티 멤버인지 여부를 boolean 으로 바로 알 수 있음.
     */
    boolean existsByPartyIdAndUserIdAndStatus(Long partyId, Long userId, Status excludedStatus);


}
