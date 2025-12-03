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
    Optional<PartyUser> findByPartyIdAndUserId(Long partyId, Long userId); //나의 파티 권한 조회

    boolean existsByPartyIdAndUserId(Long partyId, Long userId);

    long countByPartyId(Long partyId);

    Page<PartyUser> findByPartyId(Long partyId, Pageable pageable);

    Page<PartyUser> findByPartyIdAndAuthority(Long partyId, PartyAuthority authority, Pageable pageable);

    Page<PartyUser> findByPartyIdAndUser_NicknameContainingIgnoreCase(Long partyId, String nickname, Pageable pageable);

    Page<PartyUser> findByPartyIdAndAuthorityAndUser_NicknameContainingIgnoreCase(
            Long partyId, PartyAuthority authority, String nickname, Pageable pageable
    );

    Optional<PartyUser> findByParty_IdAndUser_IdAndStatusNot(Long partyId,
                                                             Long userId,
                                                             Status status);

    // 요청자(현재 로그인 유저)의 파티 참여 정보
    Optional<PartyUser> findByParty_IdAndUser_IdAndStatus(Long partyId,
                                                          Long userId,
                                                          Status status);

    // 위임 대상 파티원
    Optional<PartyUser> findByIdAndParty_IdAndStatus(Long partyUserId,
                                                     Long partyId,
                                                     Status status);


    long countByParty_IdAndStatusNot(Long partyId, Status status);

    Optional<PartyUser> findByIdAndParty_IdAndStatusNot(Long partyUserId,
                                                        Long partyId,
                                                        Status status);

    List<PartyUser> findByParty_IdAndIdInAndStatusNot(Long partyId,
                                                      List<Long> ids,
                                                      Status status);

    /**
     * 특정 파티에 특정 유저가 속해있는지 확인하는 메서드
     * (단, 삭제된 멤버는 제외)
     *
     * - party.id = :partyId
     * - user.id = :userId
     * - status != :excludedStatus
     *
     * 사용 예:
     * existsByParty_IdAndUser_IdAndStatusNot(partyId, userId, Status.DELETED)
     *
     * → 해당 유저가 파티 멤버인지 여부를 boolean 으로 바로 알 수 있음.
     */
    boolean existsByParty_IdAndUser_IdAndStatusNot(Long partyId, Long userId, Status excludedStatus);


}
