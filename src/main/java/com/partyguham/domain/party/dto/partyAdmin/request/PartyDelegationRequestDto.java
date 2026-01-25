package com.partyguham.domain.party.dto.partyAdmin.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartyDelegationRequestDto {

    /**
     * 새 파티장으로 위임할 파티원 ID (party_user.id)
     */
    @NotNull
    private Long partyUserId;
}