package com.partyguham.user.profile.dto.request;

import java.util.List;

public record BulkCareerUpdateRequest(
        List<CareerUpdateItem> careers
) {}