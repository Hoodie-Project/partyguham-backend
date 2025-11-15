package com.partyguham.auth.ott.controller;

import com.partyguham.auth.ott.model.OttPayload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ✅ OTT 테스트용 컨트롤러
 * - OTT가 제대로 인증되면 ROLE_SIGNUP 권한 + OttPayload를 확인할 수 있다.
 */
@RestController
public class OttTestController {

    @GetMapping("/api/v2/ott-test")
    @PreAuthorize("hasRole('SIGNUP')")
    public Object test(Authentication authentication) {
        // authentication 은 OttAuthenticatedToken 인스턴스일 것
        var principal = authentication.getPrincipal();
        if (principal instanceof OttPayload payload) {
            return payload; // JSON 으로 OTT 안의 내용이 찍히는지 확인해보기
        }
        return "no payload";
    }
}