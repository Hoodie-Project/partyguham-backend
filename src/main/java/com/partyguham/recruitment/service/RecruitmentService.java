package com.partyguham.recruitment.service;

import com.partyguham.party.entity.Party;
import com.partyguham.party.exception.PartyNotFoundException;
import com.partyguham.party.repository.PartyRepository;
import com.partyguham.party.service.PartyAccessService;
import com.partyguham.recruitment.dto.request.CreatePartyRecruitmentRequestDto;
import com.partyguham.recruitment.dto.request.PartyRecruitmentsRequestDto;
import com.partyguham.recruitment.dto.response.CreatePartyRecruitmentsResponseDto;
import com.partyguham.recruitment.dto.response.PartyRecruitmentResponseDto;
import com.partyguham.recruitment.dto.response.PartyRecruitmentsResponseDto;
import com.partyguham.recruitment.entity.PartyRecruitment;
import com.partyguham.recruitment.repository.PartyRecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

/**
 * 파티 모집공고 관련 비즈니스 로직을 담당하는 서비스
 * - 컨트롤러에서 사용할 메서드 시그니처만 정의해두고, 내부 구현은 추후 채웁니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentService {

    private final PartyRecruitmentRepository partyRecruitmentRepository;
    private final PartyRepository partyRepository;
    private final PartyAccessService partyAccessService;

    /**
     * 파티 모집글 목록 조회
     */
    public List<PartyRecruitmentsResponseDto.PartyRecruitmentDto> getPartyRecruitments(Long partyId,
                                                                                       PartyRecruitmentsRequestDto request) {

        partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException(partyId));


        Sort.Direction direction = request.getOrder().equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        String properties = request.getSort() != null ? request.getSort() : "createdAt";

        Sort sort = Sort.by(direction, properties);

        List<PartyRecruitment> recruitments = partyRecruitmentRepository.findByPartyId(partyId, sort);

        List<PartyRecruitment> filtered = recruitments.stream()
                .filter(recruitment -> recruitment.getCompleted() == request.isCompleted())
                .filter(recruitment -> request.getMain().isEmpty() || recruitment.getPosition().getMain().equals(request.getMain()))
                .toList();


        List<PartyRecruitmentsResponseDto.PartyRecruitmentDto> recruitmentDtos = filtered.stream()
                .map(PartyRecruitmentsResponseDto.PartyRecruitmentDto::from)
                .toList();

        return recruitmentDtos;
    }

    /**
     * 파티 모집공고 생성 (1~5개)
     */
    @Transactional
    public List<CreatePartyRecruitmentsResponseDto> createPartyRecruitment(Long partyId,
                                                                           Long userId,
                                                                           List<CreatePartyRecruitmentRequestDto> requests) {
        
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException(partyId));

        partyAccessService.checkManagerOrThrow(partyId, userId);

        return requests.stream()
                .map(request -> {
                    PartyRecruitment recruitment = PartyRecruitment.builder()
                            .party(party)
                            .title(party.getTitle())
                            .content(request.getContent())
                            .maxParticipants(request.getRecruitingCount())
                            .currentParticipants(0)
                            .completed(false)
                            .build();
                    
                    PartyRecruitment saved = partyRecruitmentRepository.save(recruitment);
                    
                    return CreatePartyRecruitmentsResponseDto.builder()
                            .id(saved.getId())
                            .content(saved.getContent())
                            .recruitingCount(saved.getMaxParticipants())
                            .recruitedCount(saved.getCurrentParticipants())
                            .status(saved.getCompleted() ? "COMPLETED" : "RECRUITING")
                            .createdAt(saved.getCreatedAt())
                            .build();
                })
                .toList();
    }

    /**
     * 파티 모집공고 단일 조회
     */
    public PartyRecruitmentResponseDto getPartyRecruitment(Long partyRecruitmentId, Long userId) {
        PartyRecruitment recruitment = partyRecruitmentRepository.findById(partyRecruitmentId)
                .orElseThrow(() -> new EntityNotFoundException("파티 모집공고가 없습니다."));

        return PartyRecruitmentResponseDto.from(recruitment);
    }
}


