package com.partyguham.recruitment.service;

import com.partyguham.catalog.repository.PositionRepository;
import com.partyguham.party.entity.Party;
import com.partyguham.party.exception.PartyNotFoundException;
import com.partyguham.party.exception.PositionNotFoundException;
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * 파티 모집공고 관련 비즈니스 로직을 담당하는 서비스
 * - 컨트롤러에서 사용할 메서드 시그니처만 정의해두고, 내부 구현은 추후 채웁니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyRecruitmentService {

    private final PartyRecruitmentRepository partyRecruitmentRepository;
    private final PartyRepository partyRepository;
    private final PositionRepository positionRepository;
    private final PartyAccessService partyAccessService;

    /**
     * 파티 모집글 목록 조회
     */
    public PartyRecruitmentsResponseDto getPartyRecruitments(Long partyId,
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
                .filter(recruitment -> recruitment.isCompleted() == request.isCompleted())
                .filter(recruitment -> request.getMain().isEmpty() || recruitment.getPosition().getMain().equals(request.getMain()))
                .toList();


        // 6. DTO 변환
        List<PartyRecruitmentsResponseDto.PartyRecruitmentDto> recruitmentDtos = filtered.stream()
                .map(this::toPartyRecruitmentDto)
                .collect(Collectors.toList());

        // 7. ResponseDto 생성
        return PartyRecruitmentsResponseDto.builder()
                .total(filtered.size())
                .partyRecruitments(recruitmentDtos)
                .build();
    }

    /**
     * PartyRecruitment 엔티티를 DTO로 변환
     */
    private PartyRecruitmentsResponseDto.PartyRecruitmentDto toPartyRecruitmentDto(PartyRecruitment recruitment) {
        Party party = recruitment.getParty();

        // PartyDto 생성
        PartyRecruitmentsResponseDto.PartyRecruitmentDto.PartyDto.PartyTypeDto partyTypeDto =
                PartyRecruitmentsResponseDto.PartyRecruitmentDto.PartyDto.PartyTypeDto.builder()
                        .type(party.getPartyType().getType())
                        .build();

        PartyRecruitmentsResponseDto.PartyRecruitmentDto.PartyDto partyDto =
                PartyRecruitmentsResponseDto.PartyRecruitmentDto.PartyDto.builder()
                        .id(party.getId())
                        .title(party.getTitle())
                        .image(party.getImage())
                        .status(party.getStatus().name())
                        .partyType(partyTypeDto)
                        .build();

        // PositionDto 생성
        // TODO: PartyRecruitment에 Position 관계가 있다면 추가
        PartyRecruitmentsResponseDto.PartyRecruitmentDto.PositionDto positionDto = null;

        // PartyRecruitmentDto 생성
        return PartyRecruitmentsResponseDto.PartyRecruitmentDto.builder()
                .id(recruitment.getId())
                .content(recruitment.getContent())
                .recruitingCount(recruitment.getMaxParticipants())
                .recruitedCount(recruitment.getCurrentParticipants())
                .status(recruitment.getIsCompleted() ? "COMPLETED" : "RECRUITING")
                .createdAt(recruitment.getCreatedAt() != null 
                        ? recruitment.getCreatedAt().toString() 
                        : null)
                .party(partyDto)
                .position(positionDto)
                .build();
    }

    /**
     * 단일 파티 모집글 조회
     */
    public PartyRecruitmentResponseDto getPartyRecruitment(Long partyRecruitmentId, Long userId) {
        // TODO: 구현 예정
        return null;
    }

    /**
     * 파티 모집글 생성
     */
    @Transactional
    public CreatePartyRecruitmentsResponseDto createPartyRecruitment(Long partyId,
                                                                     Long userId,
                                                                     CreatePartyRecruitmentRequestDto request) {
        // 1. 파티 존재 확인
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException(partyId));

        // 2. 권한 체크 (관리자 권한 필요 - MASTER 또는 DEPUTY)
        partyAccessService.checkManagerOrThrow(partyId, userId);

        // 3. Position 존재 확인 (검증용 - 현재 PartyRecruitment 엔티티에 Position 관계는 없지만, 요청 데이터 검증)
        positionRepository.findById(request.getPositionId())
                .orElseThrow(() -> new PositionNotFoundException(request.getPositionId()));

        // 4. PartyRecruitment 엔티티 생성
        // title은 Party의 title을 사용하거나, content의 일부를 사용
        // 일단 Party의 title을 사용
        PartyRecruitment recruitment = PartyRecruitment.builder()
                .party(party)
                .title(party.getTitle()) // Party의 title 사용
                .content(request.getContent())
                .maxParticipants(request.getRecruitingCount())
                .currentParticipants(0) // 초기값 0
                .isCompleted(false) // 초기값 false
                .build();

        // 5. 저장
        PartyRecruitment saved = partyRecruitmentRepository.save(recruitment);

        // 6. DTO 변환하여 반환
        return CreatePartyRecruitmentsResponseDto.builder()
                .id(saved.getId())
                .content(saved.getContent())
                .recruitingCount(saved.getMaxParticipants())
                .recruitedCount(saved.getCurrentParticipants())
                .status(saved.getIsCompleted() ? "COMPLETED" : "RECRUITING")
                .createdAt(saved.getCreatedAt() != null 
                        ? saved.getCreatedAt().toString() 
                        : null)
                .build();
    }
}


