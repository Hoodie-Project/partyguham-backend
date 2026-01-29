package com.partyguham.domain.application.service;

import com.partyguham.domain.application.dto.req.CreatePartyApplicationRequest;
import com.partyguham.domain.application.dto.req.PartyApplicantSearchRequest;
import com.partyguham.domain.application.dto.res.PartyApplicationMeResponse;
import com.partyguham.domain.application.dto.res.PartyApplicationsResponse;
import com.partyguham.domain.application.entity.PartyApplication;
import com.partyguham.domain.application.entity.PartyApplicationStatus;
import com.partyguham.domain.application.reader.PartyApplicationReader;
import com.partyguham.domain.application.repostiory.PartyApplicationQueryRepository;
import com.partyguham.domain.application.repostiory.PartyApplicationRepository;
import com.partyguham.domain.notification.event.*;
import com.partyguham.domain.party.reader.PartyReader;
import com.partyguham.global.exception.BusinessException;
import com.partyguham.domain.party.entity.Party;
import com.partyguham.domain.party.entity.PartyAuthority;
import com.partyguham.domain.party.entity.PartyUser;
import com.partyguham.domain.party.reader.PartyUserReader;
import com.partyguham.domain.party.repository.PartyUserRepository;
import com.partyguham.domain.recruitment.entity.PartyRecruitment;
import com.partyguham.domain.recruitment.reader.PartyRecruitmentReader;
import com.partyguham.domain.recruitment.repository.PartyRecruitmentRepository;
import com.partyguham.domain.user.account.entity.User;
import com.partyguham.domain.user.account.reader.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.List;

import static com.partyguham.domain.application.exception.ApplicationErrorCode.*;
import static com.partyguham.domain.party.exception.PartyUserErrorCode.PARTY_MASTER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class PartyApplicationService {

    private final PartyApplicationReader partyApplicationReader;
    private final UserReader userReader;
    private final PartyReader partyReader;
    private final PartyUserReader partyUserReader;
    private final PartyRecruitmentReader partyRecruitmentReader;

    private final PartyUserRepository partyUserRepository;
    private final PartyApplicationRepository partyApplicationRepository;
    private final PartyApplicationQueryRepository partyApplicationQueryRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 모집 지원 생성
     * - 파티원인지 확인
     * - 모집공고 유효성 확인
     * - 중복 지원 방지
     */
    @Transactional
    public void applyToRecruitment(Long partyId,
                                   Long recruitmentId,
                                   Long userId,
                                   CreatePartyApplicationRequest request) {
        User applicantUser = userReader.read(userId);

        if (partyUserReader.isMember(partyId, userId)) {
            throw new BusinessException(ALREADY_PARTY_MEMBER);
        }

        // 2) 모집공고 조회 및 유효성 확인 (Reader/Entity 메서드 활용)
        PartyRecruitment recruitment = partyRecruitmentReader.read(recruitmentId);
        recruitment.validateNotCompleted();

        // 3) 중복 지원 방지 (Reader 메서드 활용)
        partyApplicationReader.validateDuplicate(userId, recruitmentId);

        Party party = recruitment.getParty();
        User hostUser = party.getPartyUsers().stream()
                .filter(pu -> pu.getAuthority() == PartyAuthority.MASTER)
                .findFirst()
                .orElseThrow(() -> new BusinessException(PARTY_MASTER_NOT_FOUND))
                .getUser();

        // 4) 지원 생성
        PartyApplication application = PartyApplication.builder()
                .user(applicantUser)
                .partyRecruitment(recruitment)
                .message(request.getMessage())
                .build();

        partyApplicationRepository.save(application);

        // 이벤트 발행
        PartyApplicationCreatedEvent event = PartyApplicationCreatedEvent.builder()
                .partyId(party.getId())
                .partyTitle(party.getTitle())
                .partyImage(party.getImage())
                .hostUserId(hostUser.getId())
                .applicantNickname(applicantUser.getNickname())
                .fcmToken(hostUser.getFcmToken())
                .build();

        eventPublisher.publishEvent(event);
    }

    @Transactional(readOnly = true)
    public PartyApplicationMeResponse getMyApplication(Long partyId, Long recruitmentId, Long userId) {
        PartyApplication app = partyApplicationReader.readMyApplication(partyId, recruitmentId, userId);
        return PartyApplicationMeResponse.from(app);
    }

    @Transactional
    public void deleteApplication(Long partyId, Long applicationId, Long userId) {

        PartyApplication application = partyApplicationReader.readWithParty(partyId, applicationId);

        application.validateOwner(userId);
        application.validatePendingStatus();

        partyApplicationRepository.delete(application);
    }

    @Transactional(readOnly = true)
    public PartyApplicationsResponse getPartyApplications(Long partyId,
                                                             Long partyRecruitmentId,
                                                             Long userId,
                                                             PartyApplicantSearchRequest request) {

        // 1) 권한 체크 (파티장/부파티장)
        PartyUser partyUser = partyUserReader.readByPartyAndUser(partyId, userId);
        partyUser.checkManager();

        // 2) 페이징 변환 (page는 1부터 들어온다고 가정)
        int page = (request.getPage() != null ? request.getPage() : 1) - 1;
        int size = request.getSize() != null ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size);

        // 3) QueryDSL 조회
        var pageResult = partyApplicationQueryRepository
                .searchApplicants(partyId, partyRecruitmentId, request, pageable);

        // 4) DTO 변환
        return PartyApplicationsResponse.fromEntities(
                pageResult.getContent(),
                pageResult.getTotalElements()
        );
    }


    /**
     * 파티장 지원 승인 (PENDING -> PROCESSING)
     */
    @Transactional
    public void approveByManager(Long partyId, Long applicationId, Long managerUserId) {
        // 1) 파티장/부파티장 권한 체크
        PartyUser partyUser = partyUserReader.readByPartyAndUser(partyId, managerUserId);
        partyUser.checkManager();

        // 2) 지원 엔티티 조회 + 소속 파티 검증
        PartyApplication app = partyApplicationReader.readWithParty(partyId, applicationId);

        app.approveByManager();

        PartyApplicationAcceptedEvent event = PartyApplicationAcceptedEvent.builder()
                .applicantUserId(app.getUser().getId())
                .partyId(app.getPartyRecruitment().getParty().getId())
                .partyTitle(app.getPartyRecruitment().getParty().getTitle())
                .partyImage(app.getPartyRecruitment().getParty().getImage())
                .fcmToken(app.getUser().getFcmToken())
                .build();

        eventPublisher.publishEvent(event);
    }

    /**
     * 파티장 지원 거절 (PENDING -> REJECTED)
     */
    @Transactional
    public void rejectByManager(Long partyId, Long applicationId, Long managerUserId) {
        PartyUser partyUser = partyUserReader.readByPartyAndUser(partyId, managerUserId);
        partyUser.checkManager();

        // Reader 적용
        PartyApplication app = partyApplicationReader.readWithParty(partyId, applicationId);

        app.rejectByManager();

        PartyApplicationRejectedEvent event = PartyApplicationRejectedEvent.builder()
                .applicantUserId(app.getUser().getId())
                .partyId(app.getPartyRecruitment().getParty().getId())
                .partyTitle(app.getPartyRecruitment().getParty().getTitle())
                .partyImage(app.getPartyRecruitment().getParty().getImage())
                .fcmToken(app.getUser().getFcmToken())
                .build();

        eventPublisher.publishEvent(event);
    }

    /**
     * 지원자 최종 수락: PROCESSING -> APPROVED
     * 파티 합류, 파티원 합류 알람, 모집 마감시 알람
     */
    @Transactional
    public void approveByApplicant(Long partyId, Long applicationId, Long applicantUserId) {
        // 1. 신청서 기본 조회 및 검증
        PartyApplication app = partyApplicationReader.getWithUser(applicationId);
        app.validateOwner(applicantUserId);
        app.acceptByApplicant();

        // 2. 인원수 수정을 위해 Recruitment 비관적 락 적용 잠금 범위를 최소화
        Long recruitmentId = app.getPartyRecruitment().getId();
        PartyRecruitment recruitment = partyRecruitmentReader.readWithLock(recruitmentId);
        recruitment.validateNotCompleted();
        recruitment.increaseParticipant();

        // 3. 알림 및 저장을 위해 Party + PartyUser + User 를 페치 조인으로 통합 조회
        Party party = partyReader.readWithMembers(partyId);

        // 4. 파티 합류 처리
        if (!partyUserReader.isMember(partyId, applicantUserId)) {
            partyUserRepository.save(PartyUser.builder()
                    .party(party)
                    .user(app.getUser())
                    .position(recruitment.getPosition())
                    .authority(PartyAuthority.MEMBER)
                    .build());
        }

        // 5. 파티원들에게 합류 전체 알림 (N+1 문제 제거)
        for (PartyUser member : party.getPartyUsers()) {
            if (member.getUser().getId().equals(applicantUserId)) continue;

            eventPublisher.publishEvent(PartyNewMemberJoinedEvent.builder()
                    .partyUserId(member.getUser().getId())
                    .partyId(partyId)
                    .joinUserName(app.getUser().getNickname())
                    .PartyTitle(party.getTitle())
                    .partyImage(party.getImage())
                    .fcmToken(member.getUser().getFcmToken())
                    .build());
        }

        // 6. 모집 마감 처리 및 대기자 알림 이벤트 처리
        if (recruitment.getCompleted()) {
            handleRecruitmentClosed(recruitment, party);
        }
    }

    /** 모집 마감 시 대기자 처리 로직 분리 */
    private void handleRecruitmentClosed(PartyRecruitment recruitment, Party party) {
        // 알림 대상자(User)를 페치 조인으로 한 번에 조회
        List<PartyApplication> pendingApplications = partyApplicationRepository
                .findWithUserByRecruitmentAndStatusIn(recruitment,
                        List.of(PartyApplicationStatus.PENDING, PartyApplicationStatus.PROCESSING));

        if (!pendingApplications.isEmpty()) {
            // 벌크 업데이트로 상태 일괄 변경
            partyApplicationRepository.bulkUpdateStatusToClosed(recruitment.getId(), LocalDateTime.now());

            // 이벤트 발행
            for (PartyApplication application : pendingApplications) {
                eventPublisher.publishEvent(PartyRecruitmentClosedEvent.builder()
                        .applicationUserId(application.getUser().getId())
                        .partyTitle(party.getTitle())
                        .partyImage(party.getImage())
                        .fcmToken(application.getUser().getFcmToken())
                        .build());
            }
        }
    }

    /**
     * 지원자 최종 거절: PROCESSING -> DECLINED
     */
    @Transactional
    public void rejectByApplicant(Long partyId, Long applicationId, Long applicantUserId) {

        PartyApplication app = partyApplicationReader.readWithParty(partyId, applicationId);

        app.validateOwner(applicantUserId);
        app.declineByApplicant();

        // 파티 가져오기
        Party party = app.getPartyRecruitment().getParty();
        // 파티장 찾기
        User hostUser = partyUserReader.readMaster(partyId)
                .getUser();

        // 이벤트 생성
        PartyApplicationDeclinedEvent event = PartyApplicationDeclinedEvent.builder()
                .partyId(party.getId())
                .partyTitle(party.getTitle())
                .hostUserId(hostUser.getId())
                .applicantNickname(app.getUser().getNickname())
                .partyImage(party.getImage())
                .fcmToken(hostUser.getFcmToken())
                .build();

        eventPublisher.publishEvent(event);
    }
}