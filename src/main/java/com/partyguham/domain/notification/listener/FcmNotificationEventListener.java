package com.partyguham.domain.notification.listener;

import com.partyguham.domain.notification.event.*;
import com.partyguham.domain.notification.service.FcmNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * FCM 푸시 전용 리스너
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FcmNotificationEventListener {

    private final FcmNotificationService fcmNotificationService;

    /**
     * 파티 지원 완료 → 파티장에게 푸시
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPartyApplied(PartyApplicationCreatedEvent event) {
        fcmNotificationService.sendPartyApplied(
                event.getApplicantNickname(),
                event.getPartyTitle(),
                event.getFcmToken()
        );
    }

    /**
     * 지원자 파티 합류 거절 → 파티장에게 푸시
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPartyDeclined(PartyApplicationDeclinedEvent event) {
        fcmNotificationService.sendPartyDeclined(
                event.getApplicantNickname(),
                event.getPartyTitle(),
                event.getFcmToken()
        );
    }

    /**
     * 지원자 파티 최종 합류
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPartyNewMember(PartyApplicationDeclinedEvent event) {
        fcmNotificationService.PartyNewMember(
                event.getFcmToken()
        );
    }

    /**
     * 파티장 지원 수락 -> 지원자에게 푸쉬
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPartyApplicationAccepted(PartyApplicationAcceptedEvent event) {
        fcmNotificationService.sendPartyApplicationAccepted(
                event.getPartyTitle(),
                event.getFcmToken()
        );
    }

    /**
     * 파티장 지원 거절 -> 지원자에게 푸쉬
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPartyApplicationRejected(PartyApplicationRejectedEvent event) {
        fcmNotificationService.sendPartyApplicationRejected(
                event.getPartyTitle(),
                event.getFcmToken()
        );
    }

    /** 모집 마감에 이은 지원 종료 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void partyRecruitmentClosed(PartyRecruitmentClosedEvent event) {
        fcmNotificationService.sendPartyRecruitmentClosed(
                event.getPartyTitle(),
                event.getFcmToken()
        );
    }

    /** 파티 종료 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void partyFinished(PartyFinishedEvent event) {
        fcmNotificationService.sendPartyFinished(
                event.getPartyTitle(),
                event.getFcmToken()
        );
    }

    /** 파티 재활성화 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void partyReopened(PartyReopenedEvent event) {
        fcmNotificationService.sendPartyReopened(
                event.getPartyTitle(),
                event.getFcmToken()
        );
    }


    /** 파티 재활성화 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void partyInfoUpdated(PartyInfoUpdatedEvent event) {
        fcmNotificationService.sendPartyUpdated(
                event.getPartyTitle(),
                event.getFcmToken()
        );
    }

    /** 파티 유저 떠남 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void partyMemberLeft(PartyMemberLeftEvent event) {
        fcmNotificationService.sendPartyMemberLeft(
                event.getUserNickname(),
                event.getPartyTitle(),
                event.getFcmToken()
        );
    }

    /** 파티 유저 포지션 변경 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void partyMemberPositionChangedEvent(PartyMemberPositionChangedEvent event) {
        fcmNotificationService.sendPartyMemberPositionChangedEvent(
                event.getUserNickname(),
                event.getPartyTitle(),
                event.getPosition(),
                event.getFcmToken()
        );
    }

    /** 파티 유저 강퇴 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void partyMemberKickedEvent(PartyMemberKickedEvent event) {
        fcmNotificationService.partyMemberKickedEvent(
                event.getUserNickname(),
                event.getPartyTitle(),
                event.getFcmToken()
        );
    }

    /** 파티장 변경 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void partyLeaderChangedEvent(PartyLeaderChangedEvent event) {
        fcmNotificationService.partyLeaderChangedEvent(
                event.getUserNickname(),
                event.getPartyTitle(),
                event.getFcmToken()
        );
    }
}