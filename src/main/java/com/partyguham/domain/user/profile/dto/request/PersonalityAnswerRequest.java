package com.partyguham.domain.user.profile.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PersonalityAnswerRequest(
        @NotNull Long questionId,
        @NotEmpty List<Long> optionIds   // 다중 선택 허용
) {}