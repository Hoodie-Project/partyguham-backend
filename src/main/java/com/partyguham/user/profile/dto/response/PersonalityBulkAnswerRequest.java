package com.partyguham.user.profile.dto.response;

import java.util.List;

public record PersonalityBulkAnswerRequest(
        List<PersonalityAnswerItem> personalities
) {}

