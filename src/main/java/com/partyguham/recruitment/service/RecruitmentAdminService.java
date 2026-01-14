package com.partyguham.recruitment.service;

import com.partyguham.common.exception.BusinessException;
import com.partyguham.party.entity.Party;
import com.partyguham.party.reader.PartyReader;
import com.partyguham.party.service.PartyAccessService;
import com.partyguham.recruitment.dto.request.CreatePartyRecruitmentRequest;
import com.partyguham.recruitment.dto.request.PartyRecruitmentIdsBodyRequest;
import com.partyguham.recruitment.dto.response.PartyRecruitmentsResponse;
import com.partyguham.recruitment.entity.PartyRecruitment;
import com.partyguham.recruitment.exception.RecruitmentErrorCode;
import com.partyguham.recruitment.reader.PartyRecruitmentReader;
import com.partyguham.recruitment.repository.PartyRecruitmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentAdminService {

    private final PartyReader partyReader;
    private final PartyRecruitmentReader partyRecruitmentReader;

    private final PartyRecruitmentRepository partyRecruitmentRepository;
    private final PartyAccessService partyAccessService;

    /**
     * 파티 모집공고 완료 처리      
     */
    @Transactional
    public void completePartyRecruitment(Long partyId, Long partyRecruitmentId, Long userId) {
        Party party = partyReader.readParty(partyId);

        PartyRecruitment recruitment = partyRecruitmentReader.read(partyRecruitmentId);

        if (!recruitment.getParty().getId().equals(partyId)) {
            throw new BusinessException(RecruitmentErrorCode.PR_NOT_BELONG_TO_PARTY);
        }

        partyAccessService.checkManagerOrThrow(partyId, userId);

        recruitment.complete();
    }

    /**
     * 파티 모집공고 배치 완료 처리
     */
    @Transactional
    public void completePartyRecruitmentBatch(Long partyId, Long userId, PartyRecruitmentIdsBodyRequest request) {
        Party party = partyReader.readParty(partyId);

        partyAccessService.checkManagerOrThrow(partyId, userId);
       
        List<PartyRecruitment> recruitments = partyRecruitmentRepository.findAllById(request.getPartyRecruitmentIds());

        // 요청한 ID 중 일부가 존재하지 않는지 확인 (findAllById메서드는 존재하지 않는 ID는 결과에 포함되지 않음)
        if (recruitments.size() != request.getPartyRecruitmentIds().size()) {
            throw new EntityNotFoundException("일부 모집공고를 찾을 수 없습니다.");
        }

        recruitments.forEach(PartyRecruitment::complete);
    }

    /**
     * 파티 모집공고 수정
     */
    @Transactional
    public PartyRecruitmentsResponse updatePartyRecruitment(
            Long partyId, 
            Long partyRecruitmentId, 
            Long userId,
            CreatePartyRecruitmentRequest request) {

        partyReader.readParty(partyId);
        
        PartyRecruitment recruitment = partyRecruitmentRepository.findById(partyRecruitmentId)
                .orElseThrow(() -> new EntityNotFoundException("파티 모집공고가 없습니다."));

        if (!recruitment.getParty().getId().equals(partyId)) {
            throw new IllegalArgumentException("해당 파티의 모집공고가 아닙니다.");
        }

        partyAccessService.checkManagerOrThrow(partyId, userId);

        recruitment.update(request.getContent(), request.getMaxParticipants());

        return PartyRecruitmentsResponse.from(recruitment);
    }

    /**
     * 파티 모집 삭제
     */
    @Transactional
    public void deletePartyRecruitment(Long partyId, Long partyRecruitmentId, Long userId) {

        PartyRecruitment recruitment = partyRecruitmentReader.getByPartyId(partyRecruitmentId, partyId);

        partyAccessService.checkManagerOrThrow(partyId, userId);

        recruitment.delete();
    }

    /**
     * 파티 모집 다수 삭제
     */
    @Transactional
    public void deletePartyRecruitmentBatch(
            Long partyId,
            Long userId,
            PartyRecruitmentIdsBodyRequest request
    ) {
        // 파티 관리자 권한 확인
        partyAccessService.checkManagerOrThrow(partyId, userId);

        // 모집공고 조회 + 파티 소속 검증
        List<PartyRecruitment> recruitments =
                partyRecruitmentReader.readAllByIdsAndPartyId(
                        request.getPartyRecruitmentIds(),
                        partyId
                );

        // 소프트 삭제
        recruitments.forEach(PartyRecruitment::delete);
    }
}

