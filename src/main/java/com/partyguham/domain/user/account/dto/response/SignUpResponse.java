package com.partyguham.domain.user.account.dto.response;

public record SignUpResponse(
        String accessToken,
        String refreshToken
) {}