package com.partyguham.application.service;

import com.partyguham.application.dto.req.CreatePartyApplicationRequest;
import com.partyguham.application.dto.req.PartyApplicantSearchRequest;
import com.partyguham.application.dto.res.PartyApplicationMeResponse;
import com.partyguham.application.dto.res.PartyApplicationsResponse;
import com.partyguham.application.entity.PartyApplication;
import com.partyguham.application.entity.PartyApplicationStatus;
import com.partyguham.application.reader.PartyApplicationReader;
import com.partyguham.application.repostiory.PartyApplicationQueryRepository;
import com.partyguham.application.repostiory.PartyApplicationRepository;
import com.partyguham.common.entity.Status;
import com.partyguham.common.exception.BusinessException;
import com.partyguham.notification.event.*;
import com.partyguham.party.entity.Party;
import com.partyguham.party.entity.PartyAuthority;
import com.partyguham.party.entity.PartyUser;
import com.partyguham.party.repository.PartyUserRepository;
import com.partyguham.party.service.PartyAccessService;
import com.partyguham.recruitment.entity.PartyRecruitment;
import com.partyguham.recruitment.exception.RecruitmentErrorCode;
import com.partyguham.recruitment.repository.PartyRecruitmentRepository;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.reader.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.partyguham.application.exception.ApplicationErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PartyApplicationService {

    private final PartyApplicationReader partyApplicationReader;
    private final UserReader userReader;

    private final PartyAccessService partyAccessService;
    private final PartyUserRepository partyUserRepository;
    private final PartyRecruitmentRepository partyRecruitmentRepository;
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

        // 1) 이미 파티 멤버인지 체크
        boolean alreadyMember = partyUserRepository
                .existsByParty_IdAndUser_IdAndStatusNot(partyId, userId, Status.DELETED);
        if (alreadyMember) {
            throw new BusinessException(ALREADY_PARTY_MEMBER);
        }

        // 2) 모집공고 조회 및 유효성 확인 (Reader/Entity 메서드 활용)
        PartyRecruitment recruitment = partyRecruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new BusinessException(RecruitmentErrorCode.PR_NOT_FOUND));

        recruitment.validateNotCompleted(); // 엔티티 메서드 적용

        // 3) 중복 지원 방지 (Reader 메서드 활용)
        partyApplicationReader.validateDuplicate(userId, recruitmentId);

        Party party = recruitment.getParty();
        User hostUser = party.getPartyUsers().stream()
                .filter(pu -> pu.getAuthority() == PartyAuthority.MASTER)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("파티장 정보를 찾을 수 없습니다."))
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
        partyAccessService.checkManagerOrThrow(partyId, userId);

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
        partyAccessService.checkManagerOrThrow(partyId, managerUserId);

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
        partyAccessService.checkManagerOrThrow(partyId, managerUserId);

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
     * + 여기서 파티 합류 처리
     */
    @Transactional
    public void approveByApplicant(Long partyId, Long applicationId, Long applicantUserId) {
        // 1) 지원 조회 + 파티 일치 여부 검증
        PartyApplication app = partyApplicationReader.readWithParty(partyId, applicationId);

        // 2) 이 지원이 "내 것"인지 확인
        app.validateOwner(applicantUserId);

        // 3) 상태 검증: 파티장이 승인한 상태여야 함
        app.acceptByApplicant();

        // 4) 모집 정보 가져오기
        PartyRecruitment recruitment = app.getPartyRecruitment();
        Party party = recruitment.getParty();

        recruitment.validateNotCompleted();

        // 5) 파티 합류 처리
        boolean alreadyMember = partyUserRepository
                .existsByParty_IdAndUser_IdAndStatusNot(
                        party.getId(),
                        applicantUserId,
                        Status.DELETED
                );

        recruitment.increaseParticipant();

        if (!alreadyMember) {
            User user = app.getUser(); // 이미 로딩된 유저

            PartyUser partyUser = PartyUser.builder()
                    .party(party)
                    .user(user)
                    .position(recruitment.getPosition())
                    .authority(PartyAuthority.MEMBER)
                    .build();

            partyUserRepository.save(partyUser);
        }

        // 9) 파티원 전체(본인 제외)에게 "새 멤버 합류" 이벤트 발행
        List<PartyUser> members = party.getPartyUsers();
        for (PartyUser member : members) {
            Long targetUserId = member.getUser().getId();
            if (targetUserId.equals(applicantUserId)) continue; // 본인은 제외

            PartyNewMemberJoinedEvent joinedEvent = PartyNewMemberJoinedEvent.builder()
                    .partyUserId(targetUserId)
                    .partyId(party.getId())
                    .partyImage(party.getImage())
                    .fcmToken(member.getUser().getFcmToken())
                    .build();

            eventPublisher.publishEvent(joinedEvent);
        }

        // 10) 모집이 이번 승인으로 막 닫혔다면,
        //     나머지 대기/처리중 지원자들에게 "모집 마감" 이벤트 발행
        if (recruitment.getCompleted()) {
            List<PartyApplication> remainingApplicants = partyApplicationRepository
                    .findByPartyRecruitmentAndApplicationStatusIn(
                            recruitment,
                            List.of(
                                    PartyApplicationStatus.PENDING,
                                    PartyApplicationStatus.PROCESSING
                            )
                    );

            for (PartyApplication remainingApplicant : remainingApplicants) {
                User remainingUser = remainingApplicant.getUser();

                PartyRecruitmentClosedEvent closedEvent = PartyRecruitmentClosedEvent.builder()
                        .applicationUserId(remainingUser.getId())
                        .partyTitle(party.getTitle())
                        .partyImage(party.getImage())
                        .fcmToken(remainingUser.getFcmToken())
                        .build();

                eventPublisher.publishEvent(closedEvent);

                // 모집 상태 변경 있음
                 remainingApplicant.closeByRecruitment();
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
        User hostUser = partyUserRepository
                .findByParty_IdAndAuthority(partyId, PartyAuthority.MASTER)
                .orElseThrow(() -> new IllegalStateException("파티장 정보를 찾을 수 없습니다."))
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