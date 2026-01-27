package com.partyguham.domain.recruitment.reader;

import com.partyguham.global.exception.BusinessException;
import com.partyguham.domain.recruitment.entity.PartyRecruitment;
import com.partyguham.domain.recruitment.repository.PartyRecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.partyguham.domain.recruitment.exception.RecruitmentErrorCode.PR_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class PartyRecruitmentReader {
    private final PartyRecruitmentRepository partyRecruitmentRepository;

    public PartyRecruitment read(Long id) {
        return partyRecruitmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(PR_NOT_FOUND));
    }

    public PartyRecruitment readWithLock(Long id) {
        return partyRecruitmentRepository.findByIdWithLock(id)
                .orElseThrow(() -> new BusinessException(PR_NOT_FOUND));
    }

    public PartyRecruitment getByPartyId(Long id, Long partyId) {
        return partyRecruitmentRepository.findByIdAndPartyId(id, partyId)
                .orElseThrow(() -> new BusinessException(PR_NOT_FOUND));
    }


    public List<PartyRecruitment> readAllByIdsAndPartyId(
            List<Long> ids,
            Long partyId
    ) {
        List<PartyRecruitment> recruitments =
                partyRecruitmentRepository.findAllByIdInAndPartyId(ids, partyId);

        if (recruitments.size() != ids.size()) {
            throw new BusinessException(PR_NOT_FOUND);
        }

        return recruitments;
    }
}
