package com.partyguham.auth.controller;

import com.partyguham.auth.service.JwtService;
import com.partyguham.auth.service.LogoutService;
import com.partyguham.common.annotation.ApiV2Controller;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@ApiV2Controller
@RequestMapping("auth")
public class AuthController {

    private final JwtService jwtService;
    private final LogoutService logoutService;

    @GetMapping("/demo-login")
    public ResponseEntity<Map<String, String>> demoLogin() {
        // 실제로는 사용자 인증 후 userId/role 세팅
        String token = jwtService.issueAccess(1L, "USER");
        return ResponseEntity.ok(Map.of("accessToken", token));
    }

    @GetMapping("/me")
    public ResponseEntity<String> me() {
        // 필터가 SecurityContext에 인증을 채우므로 여기 오면 인증된 상태
        return ResponseEntity.ok("This is protected resource.");
    }

    // 1) 앱: JSON 본문으로 refreshToken 전달
    @PostMapping("/reissue")
    public ResponseEntity<Map<String,String>> reissue(@RequestBody Map<String,String> body) {
        String rt = body.get("refreshToken");
        String access = jwtService.reissueAccess(rt); // 검증 실패 시 예외 처리
        return ResponseEntity.ok(Map.of("accessToken", access));
    }

    // 2) 웹: HttpOnly 쿠키에서 읽기 (이름 예: REFRESH_TOKEN)
    @PostMapping("/reissue-web")
    public ResponseEntity<Map<String,String>> reissueWeb(@CookieValue("REFRESH_TOKEN") String rt) {
        String access = jwtService.reissueAccess(rt);
        return ResponseEntity.ok(Map.of("accessToken", access));
    }

    // ✅ 로그아웃 - 앱(JSON 본문으로 RT 전달)
    @PostMapping("/logout")
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
    @PostMapping("/logout-web")
    public ResponseEntity<Void> logoutWeb(
            HttpServletResponse res,
            @CookieValue(value="REFRESH_TOKEN", required=false) String rt,
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
        ResponseCookie del = ResponseCookie.from("REFRESH_TOKEN", "")
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
