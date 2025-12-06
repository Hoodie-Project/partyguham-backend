package com.partyguham.user.profile.dto.response;

import com.partyguham.catalog.entity.PersonalityOption;
import com.partyguham.catalog.entity.PersonalityQuestion;

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
