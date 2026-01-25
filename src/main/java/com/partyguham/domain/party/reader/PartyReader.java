package com.partyguham.domain.party.reader;

import com.partyguham.global.exception.BusinessException;
import com.partyguham.domain.party.entity.Party;
import com.partyguham.domain.party.entity.PartyType;
import com.partyguham.domain.party.exception.PartyErrorCode;
import com.partyguham.domain.party.repository.PartyRepository;
import com.partyguham.domain.party.repository.PartyTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartyReader {
    private final PartyRepository partyRepository;
    private final PartyTypeRepository partyTypeRepository;

    public Party readParty(Long id) {
        return partyRepository.findById(id)
                .orElseThrow(() -> new BusinessException(PartyErrorCode.PARTY_NOT_FOUND));
    }

    public PartyType readType(Long id) {
        return partyTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(PartyErrorCode.PARTY_TYPE_NOT_FOUND));
    }

}
