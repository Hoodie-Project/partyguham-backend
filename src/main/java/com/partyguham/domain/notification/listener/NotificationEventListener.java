package com.partyguham.domain.notification.listener;

import com.partyguham.domain.notification.event.*;
import com.partyguham.domain.notification.service.NotificationService;
import com.partyguham.domain.party.entity.Party;
import com.partyguham.domain.party.entity.PartyUser;
import com.partyguham.domain.party.reader.PartyReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

/**
 * Notification 관련 이벤트를 구독해서
 * 실제 Notification 엔티티 생성 + FCM 발송 등을 처리하는 리스너
 * AFTER_COMMIT: 트랜잭션 커밋이 성공한 뒤에만 알림 생성
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final PartyReader partyReader;
    private final NotificationService notificationService;


    /** 지원 알림 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPartyApplied(PartyApplicationCreatedEvent event) {
        log.info("NotificationEventListener.onPartyApplied partyId={}", event.getPartyId());

        notificationService.createPartyAppliedNotification(
                event.getHostUserId(),
                event.getApplicantNickname(),
                event.getPartyId(),
                event.getPartyTitle(),
                event.getPartyImage()
        );
    }

    /** 지원 합류 거절 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPartyApplicationDeclined(PartyApplicationDeclinedEvent event) {
        log.info("NotificationEventListener.onPartyApplicationDeclined partyId={}", event.getPartyId());

        notificationService.createPartyDeclinedNotification(
                event.getHostUserId(),
                event.getApplicantNickname(),
                event.getPartyId(),
                event.getPartyTitle(),
                event.getPartyImage()
        );
    }

    /** 지원자 수락, 최종 합류 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPartyApplicationDeclined(PartyNewMemberJoinedEvent event) {
        log.info("PartyNewMemberJoinedEvent partyId={}", event.getPartyId());

        Party party = partyReader.readWithMembers(event.getPartyId());

        for (PartyUser member : party.getPartyUsers()) {
            if (member.getUser().getId().equals(event.getJoinUserId())) continue;

            notificationService.PartyNewMemberNotification(
                    member.getUser().getId(),
                    event.getPartyId(),
                    event.getJoinUserName(),
                    party.getTitle(),
                    party.getImage()
            );
        }
    }

    /** 파티장 지원 수락 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPartyApplicationAccepted(PartyApplicationAcceptedEvent event) {
        log.info("onPartyApplicationAccepted partyId={}", event.getPartyId());

        notificationService.partyApplicationAcceptedNotification(
                event.getApplicantUserId(),
                event.getPartyTitle(),
                event.getPartyImage()
        );
    }

    /** 파티장 지원 거절 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPartyApplicationRejected(PartyApplicationRejectedEvent event) {
        log.info("onPartyApplicationRejected partyId={}", event.getPartyId());

        notificationService.partyApplicationRejectedNotification(
                event.getApplicantUserId(),
                event.getPartyTitle(),
                event.getPartyImage()
        );
    }

    /** 모집 마감에 이은 지원 종료 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void partyRecruitmentClosed(PartyRecruitmentClosedEvent event) {
        log.info("PartyRecruitmentClosedEvent");

        notificationService.PartyRecruitmentClosed(
                event.getApplicationUserId(),
                event.getPartyTitle(),
                event.getPartyImage()
        );
    }

    /** 파티 종료 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void partyFinished(PartyFinishedEvent event) {
        log.info("partyFinished");

        notificationService.partyFinished(
                event.getPartyUserId(),
                event.getPartyId(),
                event.getPartyTitle(),
                event.getPartyImage()
        );
    }

    /** 파티 재 진행중 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void partyReopened(PartyReopenedEvent event) {
        log.info("partyReopened");

        notificationService.partyReopened(
                event.getPartyUserId(),
                event.getPartyId(),
                event.getPartyTitle(),
                event.getPartyImage()
        );
    }

    /** 파티 업데이트 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void partyInfoUpdated(PartyInfoUpdatedEvent event) {
        log.info("partyReopened");

        notificationService.partyInfoUpdated(
                event.getPartyUserId(),
                event.getPartyId(),
                event.getPartyTitle(),
                event.getPartyImage()
        );
    }

    /** 파티유저 나감 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void partyMemberLeft(PartyMemberLeftEvent event) {
        log.info("partyReopened");

        notificationService.partyMemberLeft(
                event.getPartyUserId(),
                event.getUserNickname(),
                event.getPartyId(),
                event.getPartyTitle(),
                event.getPartyImage()
        );
    }

    /** 파티유저 포지션 변경*/
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void partyMemberPositionChangedEvent(PartyMemberPositionChangedEvent event) {
        log.info("partyReopened");

        notificationService.partyMemberPositionChangedEvent(
                event.getPartyUserId(),
                event.getUserNickname(),
                event.getPosition(),
                event.getPartyId(),
                event.getPartyTitle(),
                event.getPartyImage()
        );
    }

    /** 파티유저 강퇴 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void partyMemberKickedEvent(PartyMemberKickedEvent event) {
        log.info("partyReopened");

        notificationService.partyMemberKickedEvent(
                event.getPartyUserId(),
                event.getUserNickname(),
                event.getPartyId(),
                event.getPartyTitle(),
                event.getPartyImage()
        );
    }

    /** 파티장 변경 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void partyLeaderChangedEvent(PartyLeaderChangedEvent event) {
        log.info("partyReopened");

        notificationService.partyLeaderChangedEvent(
                event.getPartyUserId(),
                event.getUserNickname(),
                event.getPartyId(),
                event.getPartyTitle(),
                event.getPartyImage()
        );
    }
}
