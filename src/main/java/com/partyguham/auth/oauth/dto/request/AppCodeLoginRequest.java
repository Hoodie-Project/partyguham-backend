package com.partyguham.auth.oauth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AppCodeLoginRequest(
        @NotBlank String token
) {}