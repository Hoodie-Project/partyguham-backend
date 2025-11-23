package com.partyguham.catalog.dto.response;

import java.util.List;

public record PersonalityQuestionWithOptionsResponse(
        Long id,
        String content,
        short responseCount,
        List<OptionInfo> options
) {
    public record OptionInfo(
            Long id,
            String content
    ) {}
}