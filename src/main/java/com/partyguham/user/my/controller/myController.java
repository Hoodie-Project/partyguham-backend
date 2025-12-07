package com.partyguham.user.my.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.user.my.dto.request.GetMyPartiesRequestDto;
import com.partyguham.user.my.dto.response.GetMyPartiesResponseDto;
import com.partyguham.user.my.service.MyPartyService;
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
    // 나의 지원 목록 조회
}
