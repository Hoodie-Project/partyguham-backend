package com.partyguham.domain.party.dto.partyAdmin.response;

import com.partyguham.domain.party.entity.Party;
import com.partyguham.domain.party.entity.PartyStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdatePartyStatusResponseDto {

    private Long id;
    private PartyStatus partyStatus;

    public static UpdatePartyStatusResponseDto from(Party party) {
        return UpdatePartyStatusResponseDto.builder()
                .id(party.getId())
                .partyStatus(party.getPartyStatus())
                .build();
    }
}