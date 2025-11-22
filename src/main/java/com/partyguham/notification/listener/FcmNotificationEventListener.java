package com.partyguham.notification.listener;

import com.partyguham.infra.fcm.FcmService;
import com.partyguham.notification.event.PartyAppliedEvent;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

/**
 * FCM 푸시 전용 리스너
 * - PartyAppliedEvent 를 구독해서,
 *   파티장(hostUserId)의 fcmToken으로 푸시를 보낸다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FcmNotificationEventListener {

    private final UserRepository userRepository;
    private final FcmService fcmService;

    /**
     * 파티 지원 완료 → 파티장에게 푸시
     *
     * AFTER_COMMIT 이 기본이라,
     * 트랜잭션이 롤백되면 푸시도 나가지 않음.
     */
    @Async
    @TransactionalEventListener
    public void onPartyApplied(PartyAppliedEvent event) {

        Long hostUserId = event.getHostUserId();

        // 1) 파티장 조회
        User host = userRepository.findById(hostUserId)
                .orElse(null);

        if (host == null) {
            log.warn("[FCM] host user not found. hostUserId={}", hostUserId);
            return;
        }

        String fcmToken = host.getFcmToken();
        if (fcmToken == null || fcmToken.isBlank()) {
            log.info("[FCM] host user has no fcmToken. hostUserId={}", hostUserId);
            return;
        }

        // 2) 알림 타이틀/본문 구성
        String title = "새로운 파티 지원이 도착했어요";
        String body = String.format("'%s' 파티에 새로운 지원이 있습니다.", event.getPartyTitle());

        // 3) 딥링크/네비게이션용 data (앱에서 extras 로 파싱해서 화면 이동)
        Map<String, String> data = Map.of(
                "type", "party_applied",
                "partyId", event.getPartyId().toString()
        );

        // 4) 실제 FCM 발송
        fcmService.sendToToken(fcmToken, title, body, data);
    }
}