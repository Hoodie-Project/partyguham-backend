package com.partyguham.auth.oauth.controller;

import com.partyguham.auth.oauth.dto.response.SignupContextResponse;
import com.partyguham.auth.ott.model.OttPayload;
import com.partyguham.common.annotation.ApiV2Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ApiV2Controller
@RequestMapping("/auth")
public class SignupContextController {

    @GetMapping("/signup/context")
    @PreAuthorize("hasRole('SIGNUP')") // SIGNUP OTT 가진 사람만
    public SignupContextResponse context(@AuthenticationPrincipal OttPayload ott) {
        return new SignupContextResponse(
                ott.provider().name(),
                ott.email(),
                ott.image()
        );
    }
}