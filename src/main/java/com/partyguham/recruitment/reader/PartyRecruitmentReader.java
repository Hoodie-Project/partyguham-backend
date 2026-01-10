package com.partyguham.recruitment.reader;

import com.partyguham.common.error.exception.BusinessException;
import com.partyguham.recruitment.entity.PartyRecruitment;
import com.partyguham.recruitment.exception.RecruitmentErrorCode;
import com.partyguham.recruitment.repository.PartyRecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartyRecruitmentReader {
    private final PartyRecruitmentRepository partyRecruitmentRepository;

    public PartyRecruitment read(Long id) {
        return partyRecruitmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(RecruitmentErrorCode.PR_NOT_FOUND));
    }


}
