package com.partyguham.user.profile.dto.request;

import com.partyguham.user.profile.dto.response.PersonalityAnswerItem;

import java.util.List;

public record PersonalityBulkAnswerRequest(
        List<PersonalityAnswerItem> personalities
) {}

