package com.partyguham.user.profile.dto.request;

import com.partyguham.user.profile.dto.PersonalityAnswerDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PersonalityBulkAnswerRequest(
        @NotEmpty(message = "성향 응답 리스트가 비어 있습니다.")
        @Size(max = 10, message = "응답은 최대 10개까지 가능합니다.") // 예시 제약사항
        @Valid
        List<PersonalityAnswerDto> personalities
) {}

