package com.partyguham.recruitment.service;

import com.partyguham.common.entity.Status;
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
import com.partyguham.catalog.entity.Position;
import com.partyguham.catalog.repository.PositionRepository;
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
    private final PositionRepository positionRepository;

    /**
     * 파티 모집 목록 조회
     */
    public List<PartyRecruitmentsResponseDto> getPartyRecruitments(Long partyId,
                                                                    PartyRecruitmentsRequestDto request) {

        partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException());


        Sort.Direction direction = request.getOrder().equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        String properties = request.getSort() != null ? request.getSort() : "createdAt";

        Sort sort = Sort.by(direction, properties);

        List<PartyRecruitment> recruitments = partyRecruitmentRepository.findByPartyId(partyId, sort);

        List<PartyRecruitment> filtered = recruitments.stream()
                .filter(recruitment -> recruitment.getStatus() != Status.DELETED)  // DELETED 제외
                .filter(recruitment -> recruitment.getCompleted().equals(request.getCompleted()))
                .filter(recruitment -> {  // main 필터링
                    String main = request.getMain();
                    if (main != null && !main.isBlank()) { // main 값이 있으면 필터 적용
                        return recruitment.getPosition().getMain().equals(main);
                    }
                    return true; // main 값이 없으면 전체 조회
                })
                .toList();


        return filtered.stream()
                .map(PartyRecruitmentsResponseDto::from)
                .toList();
    }

    /**
     * 파티 모집공고 생성
     */
    @Transactional
    public CreatePartyRecruitmentsResponseDto createPartyRecruitment(Long partyId,
                                                                     Long userId,
                                                                     CreatePartyRecruitmentRequestDto request) {
        
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException());

        partyAccessService.checkManagerOrThrow(partyId, userId);

        Position position = positionRepository.findById(request.getPositionId())
                .orElseThrow(() -> new EntityNotFoundException("포지션을 찾을 수 없습니다: " + request.getPositionId()));

        PartyRecruitment recruitment = PartyRecruitment.builder()
                .party(party)
                .position(position)
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
                .completed(saved.getCompleted())
                .createdAt(saved.getCreatedAt())
                .build();
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


