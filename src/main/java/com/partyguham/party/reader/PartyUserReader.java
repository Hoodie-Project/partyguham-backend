package com.partyguham.party.reader;

import com.partyguham.common.entity.Status;
import com.partyguham.common.exception.BusinessException;
import com.partyguham.party.entity.PartyUser;
import com.partyguham.party.exception.PartyUserErrorCode;
import com.partyguham.party.repository.PartyUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartyUserReader {
    private final PartyUserRepository partyUserRepository;

    public PartyUser read(Long id) {
        return partyUserRepository.findById(id)
                .orElseThrow(() -> new BusinessException(PartyUserErrorCode.PARTY_USER_NOT_FOUND));
    }

    public PartyUser getMember(Long partyId, Long userId) {
        return partyUserRepository.findByPartyIdAndUserId(partyId, userId)
                .orElseThrow(() -> new BusinessException(PartyUserErrorCode.PARTY_USER_NOT_FOUND));
    }

    public PartyUser readByPartyIdAndUserIdAndStatus(Long partyId, Long userId, Status status) {
        return partyUserRepository.findByParty_IdAndUser_IdAndStatus(partyId, userId, status)
                .orElseThrow(() -> new BusinessException(PartyUserErrorCode.PARTY_MASTER_NOT_FOUND));
    }

    public PartyUser readByIdAndPartyIdAndStatus(Long partyUserId, Long partyId, Status status) {
        return partyUserRepository.findByIdAndParty_IdAndStatus(partyUserId, partyId, status)
                .orElseThrow(() -> new BusinessException(PartyUserErrorCode.PARTY_DELEGATION_TARGET_NOT_FOUND));
    }

    public PartyUser readByIdAndPartyIdAndStatusNot(Long partyUserId, Long partyId, Status status) {
        return partyUserRepository.findByIdAndParty_IdAndStatusNot(partyUserId, partyId, status)
                .orElseThrow(() -> new BusinessException(PartyUserErrorCode.PARTY_USER_NOT_FOUND));
    }
}
