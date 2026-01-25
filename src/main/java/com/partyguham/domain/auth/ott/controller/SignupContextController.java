package com.partyguham.domain.auth.ott.controller;

import com.partyguham.domain.auth.oauth.dto.response.SignupContextResponse;
import com.partyguham.domain.auth.ott.model.OttPayload;
import com.partyguham.global.annotation.ApiV2Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("/ott")
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