package com.partyguham.infra.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * FCM 단일 기기 푸시 발송 서비스
 * - title/body: 알림 팝업에 뜨는 내용
 * - data: 앱 내부에서 처리할 커스텀 데이터 (딥링크 등)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;

    public void sendToToken(String token,
                            String title,
                            String body,
                            Map<String, String> data) {

        // 공통 토큰 검증, 토큰 없으면 스킵
        if (token == null || token.isBlank()) {
            log.info("FCM skip: empty token");
            return;
        }

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message.Builder builder = Message.builder()
                .setToken(token)
                .setNotification(notification);

        if (data != null && !data.isEmpty()) {
            builder.putAllData(data);
        }

        Message message = builder.build();

        try {
            String response = firebaseMessaging.send(message); // 동기
            log.info("FCM sent. token={}, response={}", token, response);
        } catch (Exception e) {
            // 에러 종류에 따라 재시도/로그/모니터링 나눠서 처리
            log.error("FCM send failed. token=" + token, e);
            // 필요하면 커스텀 예외 던지기
            // throw new RuntimeException("FCM send failed", e);
        }
    }
}