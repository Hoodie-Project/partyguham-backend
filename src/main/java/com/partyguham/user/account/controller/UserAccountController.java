package com.partyguham.user.account.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.auth.ott.model.OttPayload;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.user.account.dto.request.SignUpRequest;
import com.partyguham.user.account.dto.response.MyOauthAccountResponse;
import com.partyguham.user.account.dto.response.SignUpResponse;
import com.partyguham.user.account.service.UserService;
import com.partyguham.user.account.service.UserSignupService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("users")
public class UserAccountController {

    private final UserSignupService userSignupService;
    private final UserService userService;

    @PreAuthorize("hasRole('SIGNUP')")
    @GetMapping("/check-nickname")
    public ResponseEntity<String> checkNickname(@RequestParam String nickname) {
        if (!StringUtils.hasText(nickname)) return ResponseEntity.badRequest().body("닉네임 누락");
        if (nickname == null) return ResponseEntity.status(409).body("중복된 닉네임 입니다.");
        return ResponseEntity.ok("사용가능한 닉네임 입니다.");
    }

    // 닉네임 예약 (회원가입 토큰 필요)
//    @PreAuthorize("hasRole('SIGNUP')")
//    @PostMapping("/api/v2/users/reserve-nickname")
//    public ResponseEntity<Void> reserve(@AuthenticationPrincipal OttPayload p,
//                                        @RequestBody Map<String,String> body) {
//        String nickname = body.get("nickname");
//        reservationService.reserveForSignup(p.getOauthId(), nickname, Duration.ofMinutes(10));
//        return ResponseEntity.status(201).build();
//    }

    // 필수 회원가입
    @PreAuthorize("hasRole('SIGNUP')")
    @PostMapping()
    public ResponseEntity<?> signUp(@AuthenticationPrincipal OttPayload ott,
                                    @RequestBody SignUpRequest dto,
                                    HttpServletResponse res) {

        // 1) 서비스 호출 → user 생성 + OAuthAccount 연결 + JWT 발급
        SignUpResponse result = userSignupService.signUpWithOtt(ott, dto);

        // 2) signupToken 쿠키 제거 (있다면)
        ResponseCookie delSignup = ResponseCookie.from("signupToken", "")
                .httpOnly(true).secure(true).sameSite("None").path("/").maxAge(0).build();
        res.addHeader("Set-Cookie", delSignup.toString());

        // 3) refreshToken 쿠키로 내려주기 (웹 기준)
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", result.refreshToken())
                .httpOnly(true).secure(true).sameSite("None").path("/").build();
        res.addHeader("Set-Cookie", refreshCookie.toString());

        // 4) accessToken 은 body 로 내려줌
        return ResponseEntity.status(201)
                .body(java.util.Map.of("accessToken", result.accessToken()));
    }


    // 회원탈퇴
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal UserPrincipal user) {
        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 연동한 소셜 계정 조회
     * GET /api/v2/users/me/oauth
     */
    @GetMapping("/me/oauth")
    public List<MyOauthAccountResponse> getMyOauthAccounts(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return userService.getMyOauthAccounts(user.getId());
    }
}
