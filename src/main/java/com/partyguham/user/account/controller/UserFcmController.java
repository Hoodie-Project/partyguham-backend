package com.partyguham.user.account.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.user.account.dto.request.FcmTokenRequest;
import com.partyguham.user.account.service.UserFcmService;
import com.partyguham.common.annotation.ApiV2Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@ApiV2Controller
@RequestMapping("/auth/fcm")
@RequiredArgsConstructor
public class UserFcmController {

    private final UserFcmService userFcmService;
    private final UserFcmService userFcmPushService;

    /** 클라이언트가 FCM 토큰 등록 */
    @PostMapping("/token")
    public void saveToken(@AuthenticationPrincipal UserPrincipal user,
                          @RequestBody FcmTokenRequest req) {
        userFcmService.saveToken(user.getId(), req.token());
    }

    /** 테스트용: 내 기기로 푸시 보내보기 */
    @PostMapping("/test")
    public ResponseEntity<?> testPush(@AuthenticationPrincipal UserPrincipal user) {

        userFcmPushService.sendTestToUser(user.getId());
        return ResponseEntity.ok(Map.of("message", "test push sent"));
    }
}