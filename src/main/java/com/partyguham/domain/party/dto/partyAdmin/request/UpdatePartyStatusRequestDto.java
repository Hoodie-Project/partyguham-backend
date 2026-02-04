package com.partyguham.domain.party.dto.partyAdmin.request;

import com.partyguham.domain.party.entity.PartyStatus;
import jakarta.validation.constraints.NotNull;

public record UpdatePartyStatusRequestDto(
        @NotNull(message = "partyStatus는 필수입니다.")
        PartyStatus partyStatus
) {}