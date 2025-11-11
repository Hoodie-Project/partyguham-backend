package com.partyguham.user.account.controller;


import com.partyguham.auth.ott.OttPayload;
import com.partyguham.user.account.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserAccountController {

    private final UserService signupService;

    @PreAuthorize("hasRole('SIGNUP')")
    @GetMapping("/check-nickname")
    public ResponseEntity<String> checkNickname(@RequestParam String nickname) {
        if (!StringUtils.hasText(nickname)) return ResponseEntity.badRequest().body("닉네임 누락");
        if (nickname == null) return ResponseEntity.status(409).body("중복된 닉네임 입니다.");
        return ResponseEntity.ok("사용가능한 닉네임 입니다.");
    }

    // 닉네임 예약 (회원가입 토큰 필요)
    @PreAuthorize("hasRole('SIGNUP')")
    @PostMapping("/api/v2/users/reserve-nickname")
    public ResponseEntity<Void> reserve(@AuthenticationPrincipal OttPayload p,
                                        @RequestBody Map<String,String> body) {
        String nickname = body.get("nickname");
        reservationService.reserveForSignup(p.getOauthId(), nickname, Duration.ofMinutes(10));
        return ResponseEntity.status(201).build();
    }

    // 필수 회원가입
    @PreAuthorize("hasRole('SIGNUP')")
    @PostMapping
    public Object signup(
            @AuthenticationPrincipal OttPayload payload,
            @RequestBody SignupRequest dto
    ) {
        // payload: OAuth에서 온 oauthId/email/image 등
        // dto: 닉네임/성별/생년월일 등
        return signupService.createUserAndLogin(
                payload.oauthId(), payload.email(), payload.image(),
                dto.nickname(), dto.gender(), dto.birth()
        );
    }

    // 회원탈퇴

    // 나의 소셜 계정 조회

}
