package com.partyguham.user.account.service;

import com.partyguham.infra.fcm.FcmService;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.reader.UserReader;
import com.partyguham.user.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserFcmService {

    private final UserReader userReader;
    private final UserRepository userRepository;
    private final FcmService fcmService;

    @Transactional
    public void saveToken(Long userId, String token) {
        User user = userReader.read(userId);
        user.updateFcmToken(token);
    }

    public void sendTestToUser(Long userId) {

        User user = userReader.read(userId);
        user.validateFcmToken();

        fcmService.sendToToken(
                user.getFcmToken(),
                "파티구함 테스트 알림",
                "FCM 테스트가 정상적으로 도착했어요!",
                Map.of("type", "TEST")
        );
    }
}
