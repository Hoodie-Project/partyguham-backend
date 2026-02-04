package com.partyguham.domain.recruitment.service;

import com.partyguham.domain.catalog.reader.PositionReader;
import com.partyguham.domain.party.dto.party.request.GetPartyRecruitmentsRequest;
import com.partyguham.domain.party.dto.party.response.GetPartyRecruitmentsResponse;
import com.partyguham.domain.party.entity.Party;
import com.partyguham.domain.party.entity.PartyUser;
import com.partyguham.domain.party.reader.PartyReader;
import com.partyguham.domain.party.reader.PartyUserReader;
import com.partyguham.domain.recruitment.dto.request.CreatePartyRecruitmentRequest;
import com.partyguham.domain.recruitment.dto.request.GetPartyRecruitmentsPersonalizedRequest;
import com.partyguham.domain.recruitment.dto.request.PartyRecruitmentsRequest;
import com.partyguham.domain.recruitment.dto.response.CreatePartyRecruitmentsResponse;
import com.partyguham.domain.recruitment.dto.response.PartyRecruitmentResponse;
import com.partyguham.domain.recruitment.dto.response.PartyRecruitmentsResponse;
import com.partyguham.domain.recruitment.entity.PartyRecruitment;
import com.partyguham.domain.recruitment.reader.PartyRecruitmentReader;
import com.partyguham.domain.recruitment.repository.PartyRecruitmentRepository;
import com.partyguham.domain.user.profile.entity.CareerType;
import com.partyguham.domain.user.profile.entity.UserCareer;
import com.partyguham.domain.user.profile.reader.UserProfileReader;
import com.partyguham.domain.catalog.entity.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 파티 모집공고 관련 비즈니스 로직을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentService {

    private final UserProfileReader userProfileReader;
    private final PositionReader positionReader;
    private final PartyReader partyReader;
    private final PartyUserReader partyUserReader;
    private final PartyRecruitmentReader partyRecruitmentReader;

    private final PartyRecruitmentRepository partyRecruitmentRepository;

    /**
     * [파티모집] 파티 모집 목록 조회
     * <p>
     * 특정 파티에 대한 파티모집 목록을 조회합니다.
     */
    public List<PartyRecruitmentsResponse> getPartyRecruitments(Long partyId,
                                                                PartyRecruitmentsRequest request) {

        partyReader.readParty(partyId);

        //필터링 진행 (QueryDSL로 처리) - partyId, DELETED, main, completed
        List<PartyRecruitment> recruitments = partyRecruitmentRepository.searchRecruitmentsByPartyId(partyId, request);

        return recruitments.stream()
                .map(PartyRecruitmentsResponse::from)
                .toList();
    }

    /**
     * 파티 모집공고 생성
     */
    @Transactional
    public CreatePartyRecruitmentsResponse createPartyRecruitment(Long partyId,
                                                                  Long userId,
                                                                  CreatePartyRecruitmentRequest request) {

        Party party = partyReader.readParty(partyId);
        Position position = positionReader.read(request.getPositionId());
        PartyUser partyUser = partyUserReader.readByPartyAndUser(partyId, userId);
        partyUser.checkManager();

        PartyRecruitment recruitment = PartyRecruitment.builder()
                .party(party)
                .position(position)
                .content(request.getContent())
                .maxParticipants(request.getMaxParticipants())
                .currentParticipants(0)
                .completed(false)
                .build();

        PartyRecruitment saved = partyRecruitmentRepository.save(recruitment);

        return CreatePartyRecruitmentsResponse.from(saved);
    }

    /**
     * 파티 모집공고 단일 조회
     */
    public PartyRecruitmentResponse getPartyRecruitment(Long partyRecruitmentId) {
        PartyRecruitment recruitment = partyRecruitmentReader.read(partyRecruitmentId);
        return PartyRecruitmentResponse.from(recruitment);
    }

    /**
     * [라운지] 전체 파티 모집 공고 목록 조회 (필터링, 정렬, 페이징)
     * <p>
     * 전체 파티모집 공고를 조회합니다.
     */
    public GetPartyRecruitmentsResponse getRecruitments(GetPartyRecruitmentsRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), Sort.by(request.getOrder(), request.getSort()));

        //필터링 진행 (QueryDSL로 처리) - main, completed (페이징 처리 포함)
        Page<PartyRecruitment> recruitmentPage = partyRecruitmentRepository.searchRecruitments(request, pageable);

        List<PartyRecruitmentResponse> recruitmentList = recruitmentPage.getContent().stream()
                .map(PartyRecruitmentResponse::from)
                .collect(Collectors.toList());

        return GetPartyRecruitmentsResponse.from(recruitmentPage.getTotalElements(), recruitmentList);
    }

    /**
     * 개인화된 파티 모집 공고 목록 조회 (유저의 PartyUser position의 main과 일치하는 모집공고만 조회)
     */
    public GetPartyRecruitmentsResponse getPersonalizedRecruitments(Long userId, GetPartyRecruitmentsPersonalizedRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), Sort.by(request.getOrder(), request.getSort()));

        UserCareer userCareer = userProfileReader.getCareerByType(userId, CareerType.PRIMARY);
        Long positionId = userCareer.getPosition().getId();

        Page<PartyRecruitment> recruitmentPage = partyRecruitmentRepository.searchRecruitmentsPersonalized(request, positionId, pageable);

        List<PartyRecruitmentResponse> recruitmentList = recruitmentPage.getContent().stream()
                .map(PartyRecruitmentResponse::from)
                .collect(Collectors.toList());

        return GetPartyRecruitmentsResponse.from(recruitmentPage.getTotalElements(), recruitmentList);
    }
}