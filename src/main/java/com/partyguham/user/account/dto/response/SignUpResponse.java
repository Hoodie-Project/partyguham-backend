package com.partyguham.user.account.dto.response;

public record SignUpResponse(
        String accessToken,
        String refreshToken
) {}