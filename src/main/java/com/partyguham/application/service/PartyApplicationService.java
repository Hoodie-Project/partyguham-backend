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
import com.partyguham.common.exception.NotFoundException;
import com.partyguham.notification.event.PartyApplicationCreatedEvent;
import com.partyguham.notification.event.PartyApplicationDeclinedEvent;
import com.partyguham.notification.event.PartyApplicationRejectedEvent;
import com.partyguham.notification.event.PartyNewMemberJoinedEvent;
import com.partyguham.party.entity.Party;
import com.partyguham.party.entity.PartyAuthority;
import com.partyguham.party.entity.PartyUser;
import com.partyguham.party.repository.PartyUserRepository;
import com.partyguham.party.service.PartyAccessService;
import com.partyguham.recruitment.entity.PartyRecruitment;
import com.partyguham.recruitment.repository.PartyRecruitmentRepository;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PartyApplicationService {

    private final PartyAccessService partyAccessService;
    private final PartyUserRepository partyUserRepository;
    private final UserRepository userRepository;
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
                                   CreatePartyApplicationRequestDto request) {
        // 1) 유저 조회 (기본 방어)
        User applicantUser
                = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. id=" + userId));

        // 2) 이미 파티 멤버인지 체크 (멤버면 지원 불가)
        boolean alreadyMember = partyUserRepository
                .existsByParty_IdAndUser_IdAndStatusNot(partyId, userId, Status.DELETED);
        if (alreadyMember) {
            throw new IllegalStateException("이미 해당 파티의 멤버입니다. 지원할 수 없습니다.");
        }

        // 3) 모집공고 조회
        PartyRecruitment recruitment = partyRecruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 모집공고입니다. id=" + recruitmentId
                ));
        // 모집이 이미 종료된 경우 막기
        if (Boolean.TRUE.equals(recruitment.getCompleted())) {
            throw new IllegalStateException("이미 마감된 모집입니다.");
        }

        Party party = recruitment.getParty();

        // 파티장 찾기
        User hostUser = party.getPartyUsers().stream()
                .filter(pu -> pu.getAuthority() == PartyAuthority.MASTER)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("파티장 정보를 찾을 수 없습니다."))
                .getUser();

        // 4) 중복 지원 방지
        boolean alreadyApplied = partyApplicationRepository
                .existsByUser_IdAndPartyRecruitment_Id(userId, recruitmentId);
        if (alreadyApplied) {
            throw new IllegalStateException("이미 이 모집에 지원한 사용자입니다.");
        }

        // 5) 지원 생성
        PartyApplication application = PartyApplication.builder()
                .user(applicantUser)
                .partyRecruitment(recruitment)
                .message(request.getMessage())
                .build();

        partyApplicationRepository.save(application);

        PartyApplicationCreatedEvent event = PartyApplicationCreatedEvent.builder()
                .partyId(party.getId())
                .partyTitle(party.getTitle())
                .hostUserId(hostUser.getId())
                .applicantNickname(applicantUser.getNickname())
                .fcmToken(hostUser.getFcmToken())
                .build();

        eventPublisher.publishEvent(event);
    }

    public PartyApplicationMeResponseDto getMyApplication(Long partyId,
                                                          Long recruitmentId,
                                                          Long userId) {

        PartyApplication app = partyApplicationRepository
                .findByPartyRecruitment_IdAndPartyRecruitment_Party_IdAndUser_Id(
                        recruitmentId, partyId, userId
                )
                .orElseThrow(() -> new NotFoundException(
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
        Long ownerUserId = application.getUser().getId();
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
        int size = request.getSize() != null ? request.getSize() : 20;
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


    /**
     * 파티장 지원 승인 (PENDING -> PROCESSING)
     */
    @Transactional
    public void approveByManager(Long partyId, Long applicationId, Long managerUserId) {
        // 1) 파티장/부파티장 권한 체크
        partyAccessService.checkManagerOrThrow(partyId, managerUserId);

        // 2) 지원 엔티티 조회 + 소속 파티 검증
        PartyApplication app = getApplicationForPartyOrThrow(partyId, applicationId);

        // 3) 상태 검증
        if (app.getApplicationStatus() != PartyApplicationStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태의 지원만 승인할 수 있습니다.");
        }

        // 4) 상태 변경 (응답 대기 상태)
        app.setApplicationStatus(PartyApplicationStatus.PROCESSING);
    }

    /**
     * 파티장 지원 거절 (PENDING -> REJECTED)
     */
    @Transactional
    public void rejectByManager(Long partyId, Long applicationId, Long managerUserId) {
        // 1) 권한 체크
        partyAccessService.checkManagerOrThrow(partyId, managerUserId);

        // 2) 지원 엔티티 조회 + 소속 파티 검증
        PartyApplication app = getApplicationForPartyOrThrow(partyId, applicationId);

        // 3) 상태 검증
        if (app.getApplicationStatus() != PartyApplicationStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태의 지원만 거절할 수 있습니다.");
        }

        // 4) 상태 변경
        app.setApplicationStatus(PartyApplicationStatus.REJECTED);

        PartyApplicationRejectedEvent event = PartyApplicationRejectedEvent.builder()
                .applicantUserId(app.getUser().getId())
                .partyId(app.getPartyRecruitment().getParty().getId())
                .partyTitle(app.getPartyRecruitment().getParty().getTitle())
                .fcmToken(app.getUser().getFcmToken())
                .build();

        eventPublisher.publishEvent(event);
    }

    /**
     * 공통: 지원 엔티티 조회 + 파티 일치 여부 검증
     */
    private PartyApplication getApplicationForPartyOrThrow(Long partyId, Long applicationId) {
        PartyApplication app = partyApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지원입니다. id=" + applicationId));

        // BaseEntity.status 가 DELETED 인 경우 방어
        if (app.getStatus() == Status.DELETED) {
            throw new IllegalStateException("삭제된 지원입니다.");
        }

        Long appPartyId = app.getPartyRecruitment().getParty().getId();
        if (!appPartyId.equals(partyId)) {
            throw new IllegalArgumentException("해당 파티의 지원이 아닙니다.");
        }

        return app;
    }

    /**
     * 지원자 최종 수락: PROCESSING -> APPROVED
     * + 여기서 파티 합류 처리
     */
    @Transactional
    public void approveByApplicant(Long partyId, Long applicationId, Long applicantUserId) {
        // 1) 지원 조회 + 파티 일치 여부 검증
        PartyApplication app = getApplicationForPartyOrThrow(partyId, applicationId);

        // 2) 이 지원이 "내 것"인지 확인
        Long appUserId = app.getUser().getId();
        if (!appUserId.equals(applicantUserId)) {
            throw new IllegalStateException("본인 지원만 처리할 수 있습니다.");
        }

        // 3) 상태 검증: 파티장이 승인한 상태여야 함
        if (app.getApplicationStatus() != PartyApplicationStatus.PROCESSING) {
            throw new IllegalStateException("PROCESSING 상태의 지원만 최종 승인할 수 있습니다.");
        }

        // 4) 모집 정보 가져오기
        PartyRecruitment recruitment = app.getPartyRecruitment();
        if (Boolean.TRUE.equals(recruitment.getCompleted())) {
            throw new IllegalStateException("이미 마감된 모집입니다.");
        }

        int current = recruitment.getCurrentParticipants();
        int max = recruitment.getMaxParticipants();

        if (current >= max) {
            recruitment.setCompleted(true);
            throw new IllegalStateException("모집 정원이 이미 찼습니다.");
        }

        // 5) 파티 합류 처리 (합류 되는 유저(본인)에게도 합류했다는 알림 필요할듯
        Party party = recruitment.getParty();

        boolean alreadyMember = partyUserRepository
                .existsByParty_IdAndUser_IdAndStatusNot(
                        party.getId(),
                        applicantUserId,
                        Status.DELETED
                );

        if (!alreadyMember) {
            // 아직 파티 멤버가 아니면 PartyUser 새로 생성
            User user = app.getUser(); // 이미 로딩된 유저

            PartyUser partyUser = PartyUser.builder()
                    .party(party)
                    .user(user)
                    .position(recruitment.getPosition())
                    .authority(PartyAuthority.MEMBER) // 기본 MEMBER
                    .build();

            partyUserRepository.save(partyUser);
        }

        // 6) 모집 인원 카운트 증가
        recruitment.setCurrentParticipants(current + 1);

        // 7) 정원 찼으면 모집 완료 처리
        if (recruitment.getCurrentParticipants() >= max) {
            recruitment.setCompleted(true);
        }

        // 8) 지원 상태 최종 승인 처리
        app.setApplicationStatus(PartyApplicationStatus.APPROVED);


        // 파티원 전체 조회 및 이벤트 발행
        List<PartyUser> members = party.getPartyUsers();

        for (PartyUser member : members) {
            Long targetUserId = member.getUser().getId();

            // 본인은 제외
            if (targetUserId.equals(applicantUserId)) continue;

            PartyNewMemberJoinedEvent event = PartyNewMemberJoinedEvent.builder()
                    .partyUserId(targetUserId)
                    .partyId(party.getId())
                    .fcmToken(member.getUser().getFcmToken())
                    .build();

            eventPublisher.publishEvent(event);
        }
    }

    /**
     * 지원자 최종 거절: PROCESSING -> DECLINED
     */
    @Transactional
    public void rejectByApplicant(Long partyId, Long applicationId, Long applicantUserId) {

        PartyApplication app = getApplicationForPartyOrThrow(partyId, applicationId);

        Long appUserId = app.getUser().getId();
        if (!appUserId.equals(applicantUserId)) {
            throw new IllegalStateException("본인 지원만 처리할 수 있습니다.");
        }

        if (app.getApplicationStatus() != PartyApplicationStatus.PROCESSING) {
            throw new IllegalStateException("PROCESSING 상태의 지원만 거절할 수 있습니다.");
        }

        // 파티 가져오기
        Party party = app.getPartyRecruitment().getParty();

        // 파티장 찾기
        User hostUser = partyUserRepository
                .findByParty_IdAndAuthority(partyId, PartyAuthority.MASTER)
                .orElseThrow(() -> new IllegalStateException("파티장 정보를 찾을 수 없습니다."))
                .getUser();

        // 상태 변경
        app.setApplicationStatus(PartyApplicationStatus.DECLINED);

        // 이벤트 생성
        PartyApplicationDeclinedEvent event = PartyApplicationDeclinedEvent.builder()
                .partyId(party.getId())
                .partyTitle(party.getTitle())
                .hostUserId(hostUser.getId())
                .applicantNickname(app.getUser().getNickname())
                .fcmToken(hostUser.getFcmToken()) // 필요하면
                .build();

        eventPublisher.publishEvent(event);
    }
}