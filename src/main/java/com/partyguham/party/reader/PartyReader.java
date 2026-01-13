package com.partyguham.party.reader;

import com.partyguham.common.exception.BusinessException;
import com.partyguham.party.entity.Party;
import com.partyguham.party.entity.PartyType;
import com.partyguham.party.exception.PartyErrorCode;
import com.partyguham.party.repository.PartyRepository;
import com.partyguham.party.repository.PartyTypeRepository;
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
