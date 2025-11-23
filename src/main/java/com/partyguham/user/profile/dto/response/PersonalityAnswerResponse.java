package com.partyguham.user.profile.dto.response;

import java.util.List;

public record PersonalityAnswerResponse(
        Long questionId,
        String questionContent,
        List<OptionInfo> options
) {
    public record OptionInfo(Long optionId, String content) {}
}
