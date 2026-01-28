package com.partyguham.domain.user.profile.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PersonalityAnswerDto(
        @NotNull(message = "질문 ID는 필수입니다.")
        Long questionId,

        @NotEmpty(message = "최소 하나 이상의 옵션을 선택해야 합니다.")
        List<@NotNull Long> optionIds
) {}