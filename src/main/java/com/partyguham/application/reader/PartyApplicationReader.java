package com.partyguham.application.reader;

import com.partyguham.application.entity.PartyApplication;
import com.partyguham.application.repostiory.PartyApplicationRepository;
import com.partyguham.common.entity.Status;
import com.partyguham.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.partyguham.application.exception.ApplicationErrorCode.*;

@Component
@RequiredArgsConstructor
public class PartyApplicationReader {
    private final PartyApplicationRepository partyApplicationRepository;

    public PartyApplication read(Long id) {
        return partyApplicationRepository.findByIdAndStatusNot(id, Status.DELETED)
                .orElseThrow(() -> new BusinessException(PARTY_APPLICATION_NOT_FOUND));
    }

    public PartyApplication readWithParty(Long partyId, Long id) {
        PartyApplication app = partyApplicationRepository.findWithDetailsById(id, Status.DELETED)
                .orElseThrow(() -> new BusinessException(PARTY_APPLICATION_NOT_FOUND));

        Long appPartyId = app.getPartyRecruitment().getParty().getId();

        if (!appPartyId.equals(partyId)) {
            throw new BusinessException(PARTY_APPLICATION_MISMATCHED_PARTY);
        }

        return app;
    }
}
