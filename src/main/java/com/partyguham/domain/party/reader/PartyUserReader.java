package com.partyguham.domain.party.reader;

import com.partyguham.global.entity.Status;
import com.partyguham.global.exception.BusinessException;
import com.partyguham.domain.party.entity.PartyAuthority;
import com.partyguham.domain.party.entity.PartyUser;
import com.partyguham.domain.party.repository.PartyUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.partyguham.domain.party.exception.PartyUserErrorCode.*;

@Component
@RequiredArgsConstructor
public class PartyUserReader {
    private final PartyUserRepository partyUserRepository;

    public PartyUser read(Long id) {
        return partyUserRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new BusinessException(PARTY_USER_NOT_FOUND));
    }

    public PartyUser readByPartyAndUser(Long partyId, Long userId) {
        return partyUserRepository.findByPartyIdAndUserIdAndStatus(partyId, userId, Status.ACTIVE)
                .orElseThrow(() -> new BusinessException(PARTY_USER_NOT_FOUND));
    }

    /** 특정 파티의 파티장(MASTER) '엔티티' 조회 */
    public PartyUser readMaster(Long partyId) {
        return partyUserRepository.findByPartyIdAndAuthority(partyId, PartyAuthority.MASTER)
                .orElseThrow(() -> new BusinessException(PARTY_MASTER_NOT_FOUND));
    }

    public boolean isMember(Long partyId, Long userId) {
        return partyUserRepository.existsByPartyIdAndUserIdAndStatus(partyId, userId, Status.ACTIVE);
    }
}
