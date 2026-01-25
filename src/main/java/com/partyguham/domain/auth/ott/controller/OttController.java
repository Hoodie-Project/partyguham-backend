package com.partyguham.domain.auth.ott.controller;

import com.partyguham.domain.auth.ott.model.OttPayload;
import com.partyguham.global.annotation.ApiV2Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ✅ OTT 테스트용 컨트롤러
 * - OTT가 제대로 인증되면 ROLE_SIGNUP 권한 + OttPayload를 확인할 수 있다.
 */
@Profile({"local", "dev"})
@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("ott")
public class OttController {

    @GetMapping("/signup")
    @PreAuthorize("hasRole('SIGNUP')")
    public Object signup(Authentication authentication) {
        // authentication 은 OttAuthenticatedToken 일 것
        Object principal = authentication.getPrincipal();
        if (principal instanceof OttPayload payload) {
            // 🔥 OTT 안에 들어있는 값 그대로 JSON으로 확인
            return payload;
        }
        return "no payload";
    }
}