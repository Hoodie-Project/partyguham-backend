package com.partyguham.domain.user.account.dto.response;

import lombok.Builder;

@Builder
public record MyOauthAccountResponse(
        String provider
) {}