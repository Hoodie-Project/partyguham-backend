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

    /** 파티 지원 */
    public void sendPartyApplied(
            String applicantUserNickname,
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

    /** 지원자 파티 거절 */
    public void sendPartyDeclined(
            String applicantUserNickname,
            String partyTitle,
            String fcmToken
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_APPLICATION_DECLINED;
        String title = t.title();
        String body = t.body(partyTitle, applicantUserNickname);

        Map<String, String> data = Map.of(
                "type", "PARTY"
        );

        fcmService.sendToToken(fcmToken, title, body, data);
    }

    /** 파티 지원자 수락, 최종 합류 */
    public void PartyNewMember(
            String fcmToken
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_NEW_MEMBER_JOINED;
        String title = t.title();
        String body = t.body();

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

    /**
     * 모집 완료 (종료)
     */
    public void sendPartyRecruitmentClosed(
            String partyTitle,
            String fcmToken
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_RECRUITMENT_CLOSED;
        String title = t.title();
        String body = t.body(partyTitle);

        Map<String, String> data = Map.of(
                "type", "RECRUIT"
        );

        fcmService.sendToToken(fcmToken, title, body, data);
    }

    /**
     * 파티 종료
     */
    public void sendPartyFinished(
            String partyTitle,
            String fcmToken
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_FINISHED;
        String title = t.title();
        String body = t.body(partyTitle);

        Map<String, String> data = Map.of(
                "type", "PARTY"
        );

        fcmService.sendToToken(fcmToken, title, body, data);
    }

    /**
     * 파티 재활성화
     */
    public void sendPartyReopened(
            String partyTitle,
            String fcmToken
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_REOPENED;
        String title = t.title();
        String body = t.body(partyTitle);

        Map<String, String> data = Map.of(
                "type", "PARTY"
        );

        fcmService.sendToToken(fcmToken, title, body, data);
    }

    /**
     * 파티 업데이트
     */
    public void sendPartyUpdated(
            String partyTitle,
            String fcmToken
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_INFO_UPDATED;
        String title = t.title();
        String body = t.body(partyTitle);

        Map<String, String> data = Map.of(
                "type", "PARTY"
        );

        fcmService.sendToToken(fcmToken, title, body, data);
    }

    /**
     * 파티 유저 나감
     */
    public void sendPartyMemberLeft(
            String userNickname,
            String partyTitle,
            String fcmToken
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_MEMBER_LEFT;
        String title = t.title();
        String body = t.body(partyTitle, userNickname);

        Map<String, String> data = Map.of(
                "type", "PARTY"
        );

        fcmService.sendToToken(fcmToken, title, body, data);
    }

    /**
     * 파티 유저 포지션 변경
     */
    public void sendPartyMemberPositionChangedEvent(
            String userNickname,
            String partyTitle,
            String fcmToken
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_MEMBER_POSITION_CHANGED;
        String title = t.title();
        String body = t.body(partyTitle, userNickname);

        Map<String, String> data = Map.of(
                "type", "PARTY"
        );

        fcmService.sendToToken(fcmToken, title, body, data);
    }


}
