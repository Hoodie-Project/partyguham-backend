package com.partyguham.recruitment.service;

import com.partyguham.party.entity.Party;
import com.partyguham.party.exception.PartyNotFoundException;
import com.partyguham.party.repository.PartyRepository;
import com.partyguham.party.service.PartyAccessService;
import com.partyguham.recruitment.dto.request.CreatePartyRecruitmentRequestDto;
import com.partyguham.recruitment.dto.request.PartyRecruitmentIdsBodyRequestDto;
import com.partyguham.recruitment.entity.PartyRecruitment;
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

    private final PartyRecruitmentRepository partyRecruitmentRepository;
    private final PartyRepository partyRepository;
    private final PartyAccessService partyAccessService;

    /**
     * 파티 모집공고 완료 처리
     */
    @Transactional
    public void completePartyRecruitment(Long partyId, Long partyRecruitmentId, Long userId) {
  
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException(partyId));


        PartyRecruitment recruitment = partyRecruitmentRepository.findById(partyRecruitmentId)
                .orElseThrow(() -> new EntityNotFoundException("요청한 파티 모집공고가 존재하지 않습니다."));

        if (!recruitment.getParty().getId().equals(partyId)) {
            throw new IllegalArgumentException("해당 파티의 모집공고가 아닙니다.");
        }

        partyAccessService.checkManagerOrThrow(partyId, userId);

        if (recruitment.getCompleted()) {
            throw new IllegalStateException("이미 완료된 모집공고입니다.");
        }

        recruitment.setCompleted(true);
    }

    /**
     * 파티 모집공고 배치 완료 처리
     */
    @Transactional
    public void completePartyRecruitmentBatch(Long partyId, Long userId, PartyRecruitmentIdsBodyRequestDto request) {
        
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException(partyId));

        partyAccessService.checkManagerOrThrow(partyId, userId);
       
        List<PartyRecruitment> recruitments = partyRecruitmentRepository.findAllById(request.getPartyRecruitmentIds());

        // 요청한 ID 중 일부가 존재하지 않는지 확인 (findAllById메서드는 존재하지 않는 ID는 결과에 포함되지 않음)
        if (recruitments.size() != request.getPartyRecruitmentIds().size()) { 
            throw new EntityNotFoundException("일부 모집공고를 찾을 수 없습니다.");
        }

        recruitments.forEach(recruitment -> recruitment.setCompleted(true)); // 더티체킹
    }

    /**
     * 파티 모집공고 수정
     */
    @Transactional
    public CreatePartyRecruitmentRequestDto updatePartyRecruitment(
            Long partyId, 
            Long partyRecruitmentId, 
            Long userId,
            CreatePartyRecruitmentRequestDto request) {
        
        partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException(partyId));
        
        PartyRecruitment recruitment = partyRecruitmentRepository.findById(partyRecruitmentId)
                .orElseThrow(() -> new EntityNotFoundException("파티 모집공고가 없습니다."));

        if (!recruitment.getParty().getId().equals(partyId)) {
            throw new IllegalArgumentException("해당 파티의 모집공고가 아닙니다.");
        }

        partyAccessService.checkManagerOrThrow(partyId, userId);

        recruitment.setContent(request.getContent());
        recruitment.setMaxParticipants(request.getRecruitingCount());

        return CreatePartyRecruitmentRequestDto.builder()
                .positionId(recruitment.getPosition().getId())
                .content(recruitment.getContent())
                .recruitingCount(recruitment.getMaxParticipants())
                .build();
    }

    /**
     * 파티 모집 삭제
     */
    @Transactional
    public void deletePartyRecruitment(Long partyId, Long partyRecruitmentId, Long userId) {
        partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException(partyId));

        PartyRecruitment recruitment = partyRecruitmentRepository.findById(partyRecruitmentId)
                .orElseThrow(() -> new EntityNotFoundException("파티 모집공고가 없습니다."));

        if (!recruitment.getParty().getId().equals(partyId)) {
            throw new IllegalArgumentException("해당 파티의 모집공고가 아닙니다.");
        }

        partyAccessService.checkManagerOrThrow(partyId, userId);

        partyRecruitmentRepository.delete(recruitment);
    }

    /**
     * 파티 모집 다수 삭제
     */
    @Transactional
    public void deletePartyRecruitmentBatch(Long partyId, Long userId, PartyRecruitmentIdsBodyRequestDto request) {
        partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException(partyId));

        partyAccessService.checkManagerOrThrow(partyId, userId);

        List<PartyRecruitment> recruitments = partyRecruitmentRepository.findAllById(request.getPartyRecruitmentIds());

        if (recruitments.size() != request.getPartyRecruitmentIds().size()) {
            throw new EntityNotFoundException("일부 모집공고를 찾을 수 없습니다.");
        }

        for (PartyRecruitment recruitment : recruitments) {
            if (!recruitment.getParty().getId().equals(partyId)) {
                throw new IllegalArgumentException("해당 파티의 모집공고가 아닙니다. ID: " + recruitment.getId());
            }
        }

        partyRecruitmentRepository.deleteAll(recruitments);
    }
}

