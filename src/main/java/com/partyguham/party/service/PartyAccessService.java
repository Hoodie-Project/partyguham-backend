package com.partyguham.party.service;

import com.partyguham.party.entity.PartyAuthority;
import com.partyguham.party.entity.PartyUser;
import com.partyguham.party.repository.PartyUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Set;

/**
 * 파티 소속 및 권한 공통 체크용 서비스
 */
@Service
@RequiredArgsConstructor
public class PartyAccessService {

    private final PartyUserRepository partyUserRepository;

    /** 파티 유저 엔티티 조회 (없으면 예외) */
    private PartyUser getPartyUserOrThrow(Long partyId, Long userId) {
        return partyUserRepository.findByPartyIdAndUserId(partyId, userId)
                .orElseThrow(
//                        () ->
//                        new PartyAccessDeniedException(partyId, userId, "NOT_MEMBER")
                );
    }

    /** 단순히 파티에 속해있는지만 확인 */
    public void checkMemberOrThrow(Long partyId, Long userId) {
        if (!partyUserRepository.existsByPartyIdAndUserId(partyId, userId)) {
//            throw new PartyAccessDeniedException(partyId, userId, "NOT_MEMBER");
        }
    }

    /** MASTER 또는 DEPUTY 이상인지 확인 (관리 권한 필요할 때) */
    public void checkManagerOrThrow(Long partyId, Long userId) {
        PartyUser pu = getPartyUserOrThrow(partyId, userId);
        Set<PartyAuthority> allowed = EnumSet.of(
                PartyAuthority.MASTER,
                PartyAuthority.DEPUTY
        );
        if (!allowed.contains(pu.getAuthority())) {
//            throw new PartyAccessDeniedException(partyId, userId, "NEED_MANAGER_AUTHORITY");
        }
    }

    /** MASTER만 가능해야 할 때 */
    public void checkMasterOrThrow(Long partyId, Long userId) {
        PartyUser pu = getPartyUserOrThrow(partyId, userId);
        if (pu.getAuthority() != PartyAuthority.MASTER) {
//            throw new PartyAccessDeniedException(partyId, userId, "NEED_MASTER_AUTHORITY");
        }
    }

    /** 필요하면 boolean 버전도 사용 가능 */
    public boolean isMember(Long partyId, Long userId) {
        return partyUserRepository.existsByPartyIdAndUserId(partyId, userId);
    }
}