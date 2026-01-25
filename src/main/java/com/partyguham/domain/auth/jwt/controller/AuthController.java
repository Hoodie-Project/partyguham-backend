package com.partyguham.domain.auth.jwt.controller;

import com.partyguham.domain.auth.jwt.service.JwtService;
import com.partyguham.global.annotation.ApiV2Controller;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {

    private final JwtService jwtService;

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

        String newAccessToken = jwtService.reissueAccess(refreshToken);

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    // ✅ 로그아웃 - 앱 (JSON 바디에서 RT 추출)
    @PostMapping("/app/logout")
    public ResponseEntity<Void> logoutApp(@RequestBody Map<String, String> body) {
        String rt = body.get("refreshToken");

        // RT가 있으면 Redis에서 삭제 (JwtService 내 try-catch로 안전하게 처리)
        if (rt != null) {
            jwtService.revokeRefresh(rt);
        }

        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

    // ✅ 로그아웃 - 웹 (쿠키에서 RT 추출 및 쿠키 파기)
    @PostMapping("/web/logout")
    public ResponseEntity<Void> logoutWeb(
            @CookieValue(value = "refreshToken", required = false) String rt,
            HttpServletResponse response) {

        // 1. Redis에서 RT 삭제
        if (rt != null) {
            jwtService.revokeRefresh(rt);
        }

        // 2. 브라우저 쿠키 삭제 (Max-Age 0)
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }
}
