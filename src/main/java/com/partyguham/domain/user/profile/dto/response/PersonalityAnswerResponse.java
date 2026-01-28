package com.partyguham.domain.user.profile.dto.response;

import com.partyguham.domain.catalog.entity.PersonalityOption;
import com.partyguham.domain.catalog.entity.PersonalityQuestion;

import java.util.List;

public record PersonalityAnswerResponse(
        Long questionId,
        String questionContent,
        List<OptionInfo> options
) {
    public static PersonalityAnswerResponse from(PersonalityQuestion q,
                                                 List<PersonalityOption> opts) {

        List<OptionInfo> optionInfos = opts.stream()
                .map(opt -> new OptionInfo(opt.getId(), opt.getContent()))
                .toList();

        return new PersonalityAnswerResponse(
                q.getId(),
                q.getContent(),
                optionInfos
        );
    }

    public record OptionInfo(
            Long optionId,
            String content
    ) {}
}
