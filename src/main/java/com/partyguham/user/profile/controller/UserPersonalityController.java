package com.partyguham.user.profile.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.user.profile.dto.response.PersonalityAnswerResponse;
import com.partyguham.user.profile.dto.request.PersonalityBulkAnswerRequest;
import com.partyguham.user.profile.service.UserPersonalityService;
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

    /** 성향 응답 생성/수정 (여러 질문 한 번에 Upsert) */
    @PostMapping
    public List<PersonalityAnswerResponse> saveAll(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid PersonalityBulkAnswerRequest request
    ) {
        return userPersonalityService.saveAnswers(user.getId(), request);
    }

    /** 나의 성향 전체 조회 */
    @GetMapping
    public List<PersonalityAnswerResponse> list(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return userPersonalityService.getMyAnswers(user.getId());
    }

    /** 나의 성향 전체 삭제 */
    @DeleteMapping
    public void deleteAll(@AuthenticationPrincipal UserPrincipal user) {
        userPersonalityService.deleteAllAnswers(user.getId());
    }

    /** 특정 질문에 대한 나의 성향 전체 삭제 */
    @DeleteMapping("/questions/{questionId}")
    public void deleteByQuestion(@AuthenticationPrincipal UserPrincipal user,
                                 @PathVariable Long questionId) {
        userPersonalityService.deleteAnswersByQuestion(user.getId(), questionId);
    }

    /** 특정 질문에 대한 나의 응답 하나만 삭제 */
    @DeleteMapping("/options/{optionId}")
    public void deleteByOption(@AuthenticationPrincipal UserPrincipal user,
                               @PathVariable Long optionId) {
        userPersonalityService.deleteAnswerByOption(user.getId(), optionId);
    }
}