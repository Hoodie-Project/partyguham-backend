package com.partyguham.domain.catalog.controller;

import com.partyguham.domain.catalog.dto.response.PersonalityQuestionWithOptionsResponse;
import com.partyguham.domain.catalog.service.PersonalityQuestionQueryService;
import com.partyguham.global.annotation.ApiV2Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("/personalities")
public class PersonalityController {

    private final PersonalityQuestionQueryService questionQueryService;

    /**
     * 성향 질문/옵션 전체 조회
     */
    @GetMapping()
    public List<PersonalityQuestionWithOptionsResponse> getQuestions() {
        return questionQueryService.getAllQuestionsWithOptions();
    }
}