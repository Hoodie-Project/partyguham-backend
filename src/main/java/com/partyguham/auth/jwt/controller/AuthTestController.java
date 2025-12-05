package com.partyguham.auth.jwt.controller;

import com.partyguham.auth.jwt.service.JwtService;
import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Profile({"local", "dev"})
@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("auth/test")
public class AuthTestController {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    /**
     * 닉네임으로 테스트용 토큰 발급
     * - 닉네임 유저가 있으면: 해당 유저로 토큰 발급
     * - 없으면: 유저 생성 후 그 유저로 토큰 발급
     */
    @PostMapping("/token-by-nickname")
    public ResponseEntity<Map<String, String>> issueTokenByNickname(
            @RequestParam String nickname
    ) {
        // 1) 유저 조회 or 생성
        User user = userRepository.findByNickname(nickname)
                .orElseGet(() -> {
                    String randomEmail = "dev+" + nickname + "+" +
                            UUID.randomUUID().toString().substring(0, 8) +
                            "@example.com";

                    User newUser = User.builder()
                            .email(randomEmail)
                            .nickname(nickname)
                            .build();

                    return userRepository.save(newUser);
                });

        // 2) 토큰 발급
        String accessToken = jwtService.issueAccess(user.getId(), "USER");
        String refreshToken = jwtService.issueRefresh(user.getId());

        // 3) 응답
        return ResponseEntity.ok(Map.of(
                "userId", String.valueOf(user.getId()),
                "nickname", user.getNickname(),
                "accessToken", accessToken,
                "refreshToken", refreshToken
        ));
    }

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
