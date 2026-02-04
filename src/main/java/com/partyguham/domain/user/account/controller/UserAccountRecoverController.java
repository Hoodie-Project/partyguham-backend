package com.partyguham.domain.user.account.controller;

import com.partyguham.domain.auth.jwt.service.JwtService;
import com.partyguham.domain.auth.ott.model.OttPayload;
import com.partyguham.global.annotation.ApiV2Controller;
import com.partyguham.domain.user.account.service.UserRecoverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserAccountRecoverController {

    private final UserRecoverService userRecoverService;
    private final JwtService jwtService;

    // OTT 타입: RECOVER, ROLE_RECOVER 필요
    @PreAuthorize("hasRole('RECOVER')")
    @PostMapping("/recover")
    public ResponseEntity<?> recover(@AuthenticationPrincipal OttPayload payload) {

        Long userId = payload.userId(); // OttPayload 에 포함되어 있다고 가정

        var user = userRecoverService.recoverUser(userId); // status 변경

        String accessToken  = jwtService.issueAccess(user.getId(), "USER");
        String refreshToken = jwtService.issueRefresh(user.getId());

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        ));
    }

}
