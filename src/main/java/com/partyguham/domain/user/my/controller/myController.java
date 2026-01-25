package com.partyguham.domain.user.my.controller;

import com.partyguham.domain.auth.jwt.UserPrincipal;
import com.partyguham.global.annotation.ApiV2Controller;
import com.partyguham.domain.user.my.dto.request.GetMyPartiesRequestDto;
import com.partyguham.domain.user.my.dto.request.GetMyPartyApplicationsRequestDto;
import com.partyguham.domain.user.my.dto.response.GetMyPartiesResponseDto;
import com.partyguham.domain.user.my.dto.response.GetMyPartyApplicationsResponseDto;
import com.partyguham.domain.user.my.service.MyPartyApplicationService;
import com.partyguham.domain.user.my.service.MyPartyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("users")
public class myController {

    private final MyPartyService myPartyService;
    private final MyPartyApplicationService myPartyApplicationService;
    /**
     * 내가 속한 파티 목록 조회
     * GET /api/v2/users/me/parties
     */
    @GetMapping("/me/parties")
    public ResponseEntity<GetMyPartiesResponseDto> getMyParties(
            @AuthenticationPrincipal UserPrincipal user,
            @ModelAttribute GetMyPartiesRequestDto request
    ) {
        return ResponseEntity.ok(
                myPartyService.getMyParties(user.getId(), request)
        );
    }

    /**
     * 내가 지원한 파티 모집 목록 조회
     */
    @GetMapping("/me/parties/applications")
    public ResponseEntity<GetMyPartyApplicationsResponseDto> getMyApplications(
            @AuthenticationPrincipal UserPrincipal user,
            @ModelAttribute GetMyPartyApplicationsRequestDto request
    ) {
        return ResponseEntity.ok(
                myPartyApplicationService.getMyPartyApplications(user.getId(), request)
        );
    }
}
