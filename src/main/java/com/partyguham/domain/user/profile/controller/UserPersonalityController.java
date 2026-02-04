package com.partyguham.domain.user.profile.controller;

import com.partyguham.domain.auth.jwt.UserPrincipal;
import com.partyguham.global.annotation.ApiV2Controller;
import com.partyguham.domain.user.profile.dto.response.PersonalityAnswerResponse;
import com.partyguham.domain.user.profile.dto.request.PersonalityBulkAnswerRequest;
import com.partyguham.domain.user.profile.service.UserPersonalityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("/users/me/personalities")
public class UserPersonalityController {

    private final UserPersonalityService userPersonalityService;

    /** 성향 응답 저장/수정: 여러 질문에 대한 답변을 한 번에 처리합니다. */
    @PostMapping
    public List<PersonalityAnswerResponse> saveAll(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid PersonalityBulkAnswerRequest request
    ) {
        return userPersonalityService.saveAnswers(user.getId(), request);
    }

    /** 내 성향 조회: 질문별로 선택된 옵션 리스트를 응답합니다. */
    @GetMapping
    public List<PersonalityAnswerResponse> list(@AuthenticationPrincipal UserPrincipal user) {
        return userPersonalityService.getMyAnswers(user.getId());
    }

    /** 전체 초기화: 유저의 모든 성향 데이터를 삭제합니다. */
    @DeleteMapping
    public void deleteAll(@AuthenticationPrincipal UserPrincipal user) {
        userPersonalityService.deleteAllAnswers(user.getId());
    }

    /** 질문별 삭제: 특정 질문에 대한 모든 선택을 취소합니다. */
    @DeleteMapping("/questions/{questionId}")
    public void deleteByQuestion(@AuthenticationPrincipal UserPrincipal user, @PathVariable Long questionId) {
        userPersonalityService.deleteAnswersByQuestion(user.getId(), questionId);
    }

    /** 단건 옵션 삭제: 특정 옵션 하나만 선택을 해제합니다. */
    @DeleteMapping("/options/{optionId}")
    public void deleteByOption(@AuthenticationPrincipal UserPrincipal user, @PathVariable Long optionId) {
        userPersonalityService.deleteAnswerByOption(user.getId(), optionId);
    }
}