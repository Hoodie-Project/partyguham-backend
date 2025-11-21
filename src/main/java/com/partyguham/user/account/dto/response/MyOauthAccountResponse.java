package com.partyguham.user.account.dto.response;

import lombok.Builder;

@Builder
public record MyOauthAccountResponse(
        String provider
) {}