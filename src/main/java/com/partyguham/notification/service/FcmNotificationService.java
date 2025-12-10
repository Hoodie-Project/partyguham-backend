package com.partyguham.notification.service;

import com.partyguham.infra.fcm.FcmService;
import com.partyguham.notification.template.NotificationTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmNotificationService {
    private final FcmService fcmService;

    public void sendPartyApplied(
            String applicantUserNickname,
            Long partyId,
            String partyTitle,
            String fcmToken
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_APPLICATION_CREATED;
        String title = t.title();
        String body = t.body(partyTitle, applicantUserNickname);

        Map<String, String> data = Map.of(
                "type", "PARTY"
        );

        fcmService.sendToToken(fcmToken, title, body, data);
    }

    /** 파티장 수락시 */
    public void sendPartyApplicationAccepted(
            String partyTitle,
            String fcmToken
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_APPLICATION_ACCEPTED;
        String title = t.title();
        String body = t.body(partyTitle);

        Map<String, String> data = Map.of(
                "type", "PARTY"
        );

        fcmService.sendToToken(fcmToken, title, body, data);
    }

    /** 파티장 거절시 */
    public void sendPartyApplicationRejected(
            String partyTitle,
            String fcmToken
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_APPLICATION_REJECTED;
        String title = t.title();
        String body = t.body(partyTitle);

        Map<String, String> data = Map.of(
                "type", "PARTY"
        );

        fcmService.sendToToken(fcmToken, title, body, data);
    }
}
