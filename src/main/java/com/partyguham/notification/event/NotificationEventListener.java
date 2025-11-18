package com.partyguham.notification.event;

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
 *
 * 주의:
 *  - @Async 사용하려면 @EnableAsync 설정 필요
 *  - AFTER_COMMIT: 트랜잭션 커밋이 성공한 뒤에만 알림 생성
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

        log.debug("PartyAppliedEvent received: {}", event);

        // NotificationService 내부에서:
        //  - notification_type 조회 (예: PARTY_APPLIED)
        //  - Notification 엔티티 저장
        //  - 필요하면 FCM 푸시 발송
        notificationService.createPartyAppliedNotification(
                event.getHostUserId(),      // 알림 받을 유저
                event.getApplicantUserId(), // 지원한 유저
                event.getPartyId(),
                event.getPartyTitle()
        );
    }
}