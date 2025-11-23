package com.partyguham.user.account.service;

import com.partyguham.infra.fcm.FcmService;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserFcmService {

    private final UserRepository userRepository;
    private final FcmService fcmService;

    @Transactional
    public void saveToken(Long userId, String token) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));
        u.setFcmToken(token);
    }

    public void sendTestToUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        if (user.getFcmToken() == null || user.getFcmToken().isBlank()) {
            throw new IllegalStateException("User does not have a registered FCM token");
        }

        fcmService.sendToToken(
                user.getFcmToken(),
                "파티구함 테스트 알림",
                "FCM 테스트가 정상적으로 도착했어요!",
                Map.of("type", "TEST")
        );
    }
}
