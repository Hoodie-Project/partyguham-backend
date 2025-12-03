package com.partyguham.application.service;

import com.partyguham.application.dto.req.CreatePartyApplicationRequestDto;
import com.partyguham.application.dto.req.PartyApplicantSearchRequestDto;
import com.partyguham.application.dto.res.PartyApplicationMeResponseDto;
import com.partyguham.application.dto.res.PartyApplicationsResponseDto;
import com.partyguham.application.entity.PartyApplication;
import com.partyguham.application.entity.PartyApplicationStatus;
import com.partyguham.application.repostiory.PartyApplicationQueryRepository;
import com.partyguham.application.repostiory.PartyApplicationRepository;
import com.partyguham.common.entity.Status;
import com.partyguham.party.entity.PartyUser;
import com.partyguham.party.repository.PartyUserRepository;
import com.partyguham.party.service.PartyAccessService;
import com.partyguham.recruitment.entity.PartyRecruitment;
import com.partyguham.recruitment.repository.PartyRecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PartyApplicationService {

    private final PartyAccessService partyAccessService;
    private final PartyUserRepository partyUserRepository;
    private final PartyRecruitmentRepository partyRecruitmentRepository;
    private final PartyApplicationRepository partyApplicationRepository;
    private final PartyApplicationQueryRepository partyApplicationQueryRepository;

    /**
     * 모집 지원 생성
     * - 파티원인지 확인
     * - 모집공고 유효성 확인
     * - 중복 지원 방지
     */
    public void applyToRecruitment(Long partyId,
                                   Long recruitmentId,
                                   Long userId,
                                   CreatePartyApplicationRequestDto request) {

        // 1) 유저가 해당 파티의 파티원인지 확인 (DELETED 제외)
        PartyUser partyUser = partyUserRepository
                .findByParty_IdAndUser_IdAndStatusNot(partyId, userId, Status.DELETED)
                .orElseThrow(() -> new IllegalArgumentException(
                        "파티원이 아닙니다. partyId=" + partyId + ", userId=" + userId
                ));

        // 2) 모집공고 조회
        PartyRecruitment recruitment = partyRecruitmentRepository
                .findById(recruitmentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 모집공고입니다. id=" + recruitmentId
                ));

        // 3) 중복 지원 방지
        boolean exists = partyApplicationRepository
                .existsByPartyUser_IdAndPartyRecruitment_Id(
                        partyUser.getId(), recruitmentId
                );

        if (exists) {
            throw new IllegalStateException("이미 이 모집에 지원한 사용자입니다.");
        }

        // 4) 지원 생성
        PartyApplication application = PartyApplication.builder()
                .partyUser(partyUser)
                .partyRecruitment(recruitment)
                .message(request.getMessage())
                .build(); // BaseEntity.status = ACTIVE 기본값

        partyApplicationRepository.save(application);
    }

    public PartyApplicationMeResponseDto getMyApplication(Long partyId,
                                                          Long recruitmentId,
                                                          Long userId) {

        PartyApplication app = partyApplicationRepository
                .findByPartyRecruitment_IdAndPartyRecruitment_Party_IdAndPartyUser_User_Id(
                        recruitmentId, partyId, userId
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 모집에 대한 나의 지원 내역이 없습니다."
                ));

        return PartyApplicationMeResponseDto.from(app);
    }

    @Transactional
    public void deleteApplication(Long partyId,
                                  Long applicationId,
                                  Long userId) {

        // 1) 지원 조회
        PartyApplication application = partyApplicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 지원입니다. id=" + applicationId
                ));

        // 2) 파티 일치 여부 검사
        if (!application.getPartyRecruitment().getParty().getId().equals(partyId)) {
            throw new IllegalArgumentException("해당 파티의 지원이 아닙니다.");
        }

        // 3) 지원자 본인 확인
        Long ownerUserId = application.getPartyUser().getUser().getId();
        if (!ownerUserId.equals(userId)) {
            throw new IllegalStateException("본인이 제출한 지원만 삭제할 수 있습니다.");
        }

        // 4) 상태 체크: PENDING일 때만 삭제 가능
        if (application.getApplicationStatus() != PartyApplicationStatus.PENDING) {
            throw new IllegalStateException("검토중(pending) 상태만 취소가 가능합니다.");
        }

        // 5) 하드 삭제
        partyApplicationRepository.delete(application);
    }

    @Transactional(readOnly = true)
    public PartyApplicationsResponseDto getPartyApplications(Long partyId,
                                                             Long partyRecruitmentId,
                                                             Long userId,
                                                             PartyApplicantSearchRequestDto request) {

        // 1) 권한 체크 (파티장/부파티장)
        partyAccessService.checkManagerOrThrow(partyId, userId);

        // 2) 페이징 변환 (page는 1부터 들어온다고 가정)
        int page = (request.getPage() != null ? request.getPage() : 1) - 1;
        int size = request.getLimit() != null ? request.getLimit() : 20;
        Pageable pageable = PageRequest.of(page, size);

        // 3) QueryDSL 조회
        var pageResult = partyApplicationQueryRepository
                .searchApplicants(partyId, partyRecruitmentId, request, pageable);

        // 4) DTO 변환
        return PartyApplicationsResponseDto.fromEntities(
                pageResult.getContent(),
                pageResult.getTotalElements()
        );
    }
}