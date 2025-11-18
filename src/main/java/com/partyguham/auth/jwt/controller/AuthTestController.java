package com.partyguham.auth.jwt.controller;

import com.partyguham.auth.jwt.service.JwtService;
import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.auth.jwt.service.LogoutService;
import com.partyguham.common.annotation.ApiV2Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@ApiV2Controller
@RequestMapping("auth/test")
public class AuthTestController {

    private final JwtService jwtService;
    private final LogoutService logoutService;

    @GetMapping("/accessToken")
    public ResponseEntity<Map<String, String>> testAccessToken() {
        // 실제로는 사용자 인증 후 userId/role 세팅
        String token = jwtService.issueAccess(1L, "USER");
        return ResponseEntity.ok(Map.of("accessToken", token));
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<Map<String, String>> testRefreshToken() {
        // 실제로는 사용자 인증 후 userId/role 세팅
        String token = jwtService.issueRefresh(1L);
        return ResponseEntity.ok(Map.of("refreshToken", token));
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(
            @CookieValue(value = "refreshToken", required = false) String rtCookie,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        // 1) 헤더에서 Bearer RT 추출
        String rtHeader = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            rtHeader = authHeader.substring(7);
        }

        // 2) 둘 다 없으면 401
        if (rtCookie == null && rtHeader == null) {
            return ResponseEntity.status(401).body(Map.of("error", "refreshToken_missing"));
        }

        // 3) 둘 다 있으면 정책 결정 (에러로)
        if (rtCookie != null && rtHeader != null) {
            return ResponseEntity.badRequest().body(Map.of("error", "multiple_refresh_token_sources"));
        }

        String refreshToken = (rtCookie != null) ? rtCookie : rtHeader;

        // 4) 서버 저장소(REDIS/DB)에 존재&유효 여부 체크
//        if (!refreshTokenService.isValid(refreshToken)) {
//            return ResponseEntity.status(401).body(Map.of("error", "invalid_refresh_token"));
//        }

        // 5) RT에서 userId 꺼내고 AccessToken 재발급
        Long userId = jwtService.parse(refreshToken).getPayload().getSubject() != null
                ? Long.valueOf(jwtService.parse(refreshToken).getPayload().getSubject())
                : null;

        String newAccess = jwtService.issueAccess(userId, "USER");

        return ResponseEntity.ok(Map.of("accessToken", newAccess));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/login")
    public ResponseEntity<Map<String, UserPrincipal>> testLogin(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(Map.of("test", user));
    }

}
