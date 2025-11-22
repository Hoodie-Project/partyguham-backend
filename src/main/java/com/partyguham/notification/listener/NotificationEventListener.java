package com.partyguham.notification.listener;

import com.partyguham.notification.event.PartyAppliedEvent;
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

    /**
     * 파티 지원 이벤트를 구독하여 알림 생성
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPartyApplied(PartyAppliedEvent event) {
        log.info("NotificationEventListener.onPartyApplied partyId={}", event.getPartyId());

        notificationService.createPartyAppliedNotification(
                event.getHostUserId(),
                event.getApplicantUserId(),
                event.getPartyId(),
                event.getPartyTitle()
        );
    }
}