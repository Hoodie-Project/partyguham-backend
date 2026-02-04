package com.partyguham.domain.user.account.controller;

import com.partyguham.domain.auth.jwt.UserPrincipal;
import com.partyguham.domain.user.account.dto.request.FcmTokenRequest;
import com.partyguham.domain.user.account.service.UserFcmService;
import com.partyguham.global.annotation.ApiV2Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@ApiV2Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserFcmController {

    private final UserFcmService userFcmService;
    private final UserFcmService userFcmPushService;

    /** 클라이언트가 FCM 토큰 등록 */
    @PostMapping("/me/fcm-token")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveToken(@AuthenticationPrincipal UserPrincipal user,
                          @RequestBody FcmTokenRequest req) {
        userFcmService.saveToken(user.getId(), req.fcmToken());
    }

    /** 테스트용: 저장된 토큰으로 푸시 보내보기 */
    @Profile({"local", "dev"})
    @PostMapping("/fcm/test")
    public ResponseEntity<?> testPush(@AuthenticationPrincipal UserPrincipal user) {

        userFcmPushService.sendTestToUser(user.getId());
        return ResponseEntity.ok(Map.of("message", "test push sent"));
    }
}