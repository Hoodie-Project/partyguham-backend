package com.partyguham.notification.listener;

import com.partyguham.infra.fcm.FcmService;
import com.partyguham.notification.event.PartyAppliedEvent;
import com.partyguham.notification.service.FcmNotificationService;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

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
    public void onPartyApplied(PartyAppliedEvent event) {
        fcmNotificationService.sendPartyApplied(
                event.getApplicantNickname(),
                event.getPartyId(),
                event.getPartyTitle(),
                event.getFcmToken()
        );
    }
}