package com.partyguham.auth.oauth.dto.response;

// DTO
public record SignupContextResponse(
        String provider,
        String email,
        String image
) {}