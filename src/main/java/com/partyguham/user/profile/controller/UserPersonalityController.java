package com.partyguham.user.profile.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.user.profile.dto.request.PersonalityAnswerRequest;
import com.partyguham.user.profile.dto.response.PersonalityAnswerResponse;
import com.partyguham.user.profile.service.UserPersonalityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me/personalities")
public class UserPersonalityController {

    private final UserPersonalityService userPersonalityService;

    /** 성향 응답 생성/수정 (질문 하나에 대해 여러 옵션 선택 가능) */
    @PostMapping
    public PersonalityAnswerResponse save(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid PersonalityAnswerRequest request
    ) {
        return userPersonalityService.saveAnswer(user.getId(), request);
    }

    /** 내 성향 전체 조회 */
    @GetMapping
    public List<PersonalityAnswerResponse> list(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return userPersonalityService.getMyAnswers(user.getId());
    }

    /** 특정 질문에 대한 내 성향 삭제 */
    @DeleteMapping("/{questionId}")
    public void delete(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long questionId
    ) {
        userPersonalityService.deleteAnswer(user.getId(), questionId);
    }
}