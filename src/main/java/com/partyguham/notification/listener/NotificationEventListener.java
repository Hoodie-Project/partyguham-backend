package com.partyguham.notification.listener;

import com.partyguham.notification.event.PartyApplicationAcceptedEvent;
import com.partyguham.notification.event.PartyApplicationDeclinedEvent;
import com.partyguham.notification.event.PartyApplicationRejectedEvent;
import com.partyguham.notification.event.PartyApplicationCreatedEvent;
import com.partyguham.notification.service.NotificationService;
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
                event.getPartyTitle()
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
                event.getPartyTitle()
        );
    }

    /** 파티장 지원 수락 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPartyApplicationAccepted(PartyApplicationAcceptedEvent event) {
        log.info("onPartyApplicationAccepted partyId={}", event.getPartyId());

        notificationService.partyApplicationAcceptedNotification(
                event.getApplicantUserId(),
                event.getPartyTitle()
        );
    }

    /** 파티장 지원 거절 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPartyApplicationRejected(PartyApplicationRejectedEvent event) {
        log.info("onPartyApplicationRejected partyId={}", event.getPartyId());

        notificationService.partyApplicationRejectedNotification(
                event.getApplicantUserId(),
                event.getPartyTitle()
        );
    }
}