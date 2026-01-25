package com.partyguham.auth.jwt.controller;

import com.partyguham.auth.jwt.service.JwtService;
import com.partyguham.auth.jwt.service.LogoutService;
import com.partyguham.common.annotation.ApiV2Controller;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
    private final LogoutService logoutService;

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

    // ✅ 로그아웃 - 앱(JSON 본문으로 RT 전달)
    @PostMapping("/app/logout")
    public ResponseEntity<Void> logout(@RequestBody Map<String,String> body,
                                       @RequestHeader(value="Authorization", required=false) String auth) {
        String rt = body.get("refreshToken");
        if (rt != null) logoutService.revokeRefresh(rt);
        String at = (auth!=null && auth.startsWith("Bearer ")) ? auth.substring(7) : null;
        if (at != null) logoutService.blacklistAccess(at); // 선택
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

    // ✅ 로그아웃 - 웹(쿠키 RT 제거)
    @PostMapping("/web/logout")
    public ResponseEntity<Void> logoutWeb(
            HttpServletResponse res,
            @CookieValue(value="refreshToken", required=false) String rt,
            @RequestHeader(value="Authorization", required=false) String auth) {

        // Refresh Token 무효화 (쿠키)
        if (rt != null) logoutService.revokeRefresh(rt);

        // Access Token 블랙리스트 (옵션)
        String at = null;
        if (auth != null && auth.startsWith("Bearer ")) {
            at = auth.substring(7);
            logoutService.blacklistAccess(at); // 선택
        }

        // Refresh Token 쿠키 삭제
        ResponseCookie del = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();
        res.addHeader("Set-Cookie", del.toString());

        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }
}
