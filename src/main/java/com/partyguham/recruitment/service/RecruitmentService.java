package com.partyguham.recruitment.service;

import com.partyguham.party.dto.party.request.GetPartyRecruitmentsRequestDto;
import com.partyguham.party.dto.party.response.GetPartyRecruitmentsResponseDto;
import com.partyguham.party.entity.Party;
import com.partyguham.party.exception.PartyNotFoundException;
import com.partyguham.party.repository.PartyRepository;
import com.partyguham.party.service.PartyAccessService;
import com.partyguham.recruitment.dto.request.CreatePartyRecruitmentRequestDto;
import com.partyguham.recruitment.dto.request.GetPartyRecruitmentsPersonalizedRequestDto;
import com.partyguham.recruitment.dto.request.PartyRecruitmentsRequestDto;
import com.partyguham.recruitment.dto.response.CreatePartyRecruitmentsResponseDto;
import com.partyguham.recruitment.dto.response.PartyRecruitmentDto;
import com.partyguham.recruitment.dto.response.PartyRecruitmentResponseDto;
import com.partyguham.recruitment.dto.response.PartyRecruitmentsResponseDto;
import com.partyguham.recruitment.entity.PartyRecruitment;
import com.partyguham.recruitment.repository.PartyRecruitmentRepository;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.repository.UserRepository;
import com.partyguham.user.profile.entity.CareerType;
import com.partyguham.user.profile.entity.UserCareer;
import com.partyguham.user.profile.repository.UserCareerRepository;
import com.partyguham.catalog.entity.Position;
import com.partyguham.catalog.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 파티 모집공고 관련 비즈니스 로직을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentService {

    private final PartyRecruitmentRepository partyRecruitmentRepository;
    private final PartyRepository partyRepository;
    private final PartyAccessService partyAccessService;
    private final PositionRepository positionRepository;
    private final UserCareerRepository userCareerRepository;
    private final UserRepository userRepository;

    /**
     * [파티모집] 파티 모집 목록 조회
     *
     * 특정 파티에 대한 파티모집 목록을 조회합니다.
     */
    public List<PartyRecruitmentsResponseDto> getPartyRecruitments(Long partyId,
                                                                    PartyRecruitmentsRequestDto request) {

        partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException());

        //필터링 진행 (QueryDSL로 처리) - partyId, DELETED, main, completed
        List<PartyRecruitment> recruitments = partyRecruitmentRepository.searchRecruitmentsByPartyId(partyId, request);

        return recruitments.stream()
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
    public PartyRecruitmentResponseDto getPartyRecruitment(Long partyRecruitmentId) {
        PartyRecruitment recruitment = partyRecruitmentRepository.findById(partyRecruitmentId)
                .orElseThrow(() -> new EntityNotFoundException("파티 모집공고가 없습니다."));

        return PartyRecruitmentResponseDto.from(recruitment);
    }

    /**
     * [라운지] 전체 파티 모집 공고 목록 조회 (필터링, 정렬, 페이징)
     *
     * 전체 파티모집 공고를 조회합니다.
     */
    public GetPartyRecruitmentsResponseDto getRecruitments(GetPartyRecruitmentsRequestDto request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), Sort.by(request.getOrder(), request.getSort()));

        //필터링 진행 (QueryDSL로 처리) - main, completed (페이징 처리 포함)
        Page<PartyRecruitment> recruitmentPage = partyRecruitmentRepository.searchRecruitments(request, pageable);

        List<PartyRecruitmentDto> recruitmentList = recruitmentPage.getContent().stream()
                .map(PartyRecruitmentDto::from)
                .collect(Collectors.toList());

        return GetPartyRecruitmentsResponseDto.builder()
                .total(recruitmentPage.getTotalElements())
                .partyRecruitments(recruitmentList)
                .build();
    }

    /**
     * 개인화된 파티 모집 공고 목록 조회 (유저의 PartyUser position의 main과 일치하는 모집공고만 조회)
     */
    public GetPartyRecruitmentsResponseDto getPersonalizedRecruitments(Long userId, GetPartyRecruitmentsPersonalizedRequestDto request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), Sort.by(request.getOrder(), request.getSort()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다: " + userId));

        UserCareer userCareer = userCareerRepository.findByUserAndCareerType(user, CareerType.PRIMARY)
                .orElseThrow(() -> new EntityNotFoundException("유저의 커리어를 찾을 수 없습니다: " + userId));

        Long positionId = userCareer.getPosition().getId();

        Page<PartyRecruitment> recruitmentPage = partyRecruitmentRepository.searchRecruitmentsPersonalized(request, positionId, pageable);
        
        List<PartyRecruitmentDto> recruitmentList = recruitmentPage.getContent().stream()
                .map(PartyRecruitmentDto::from)
                .collect(Collectors.toList());

        return GetPartyRecruitmentsResponseDto.builder()
                .total(recruitmentPage.getTotalElements())
                .partyRecruitments(recruitmentList)
                .build();
    }
}


