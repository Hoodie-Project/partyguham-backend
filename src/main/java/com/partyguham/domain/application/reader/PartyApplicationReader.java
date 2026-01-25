package com.partyguham.domain.application.reader;

import com.partyguham.domain.application.entity.PartyApplication;
import com.partyguham.domain.application.repostiory.PartyApplicationRepository;
import com.partyguham.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.partyguham.domain.application.exception.ApplicationErrorCode.*;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyApplicationReader {
    private final PartyApplicationRepository partyApplicationRepository;

    /** 기본 단건 조회 */
    public PartyApplication read(Long id) {
        return partyApplicationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(PARTY_APPLICATION_NOT_FOUND));
    }

    /** 파티 ID와 대조하며 지원서 상세 조회 */
    public PartyApplication readWithParty(Long partyId, Long id) {
        PartyApplication app = partyApplicationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(PARTY_APPLICATION_NOT_FOUND));

        if (!app.getPartyRecruitment().getParty().getId().equals(partyId)) {
            throw new BusinessException(PARTY_APPLICATION_MISMATCHED_PARTY);
        }
        return app;
    }

    /** 내 지원서 단건 조회 */
    public PartyApplication readMyApplication(Long partyId, Long recruitmentId, Long userId) {
        return partyApplicationRepository
                .findByPartyRecruitment_IdAndPartyRecruitment_Party_IdAndUser_Id(recruitmentId, partyId, userId)
                .orElseThrow(() -> new BusinessException(PARTY_APPLICATION_NOT_FOUND));
    }

    /** 중복 지원 체크 */
    public void validateDuplicate(Long userId, Long recruitmentId) {
        if (partyApplicationRepository.existsByUser_IdAndPartyRecruitment_Id(userId, recruitmentId)) {
            throw new BusinessException(ALREADY_APPLIED);
        }
    }
}