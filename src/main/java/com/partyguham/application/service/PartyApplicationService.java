package com.partyguham.application.service;

import com.partyguham.application.dto.req.CreatePartyApplicationRequestDto;
import com.partyguham.application.entity.PartyApplication;
import com.partyguham.application.repostiory.PartyApplicationRepository;
import com.partyguham.common.entity.Status;
import com.partyguham.party.entity.PartyUser;
import com.partyguham.party.repository.PartyUserRepository;
import com.partyguham.recruitment.entity.PartyRecruitment;
import com.partyguham.recruitment.repository.PartyRecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PartyApplicationService {

    private final PartyUserRepository partyUserRepository;
    private final PartyRecruitmentRepository partyRecruitmentRepository;
    private final PartyApplicationRepository partyApplicationRepository;

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
}