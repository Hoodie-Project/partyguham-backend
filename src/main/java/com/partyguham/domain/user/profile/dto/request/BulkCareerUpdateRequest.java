package com.partyguham.domain.user.profile.dto.request;

import java.util.List;

public record BulkCareerUpdateRequest(
        List<CareerUpdateItem> careers
) {}