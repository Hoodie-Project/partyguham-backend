package com.partyguham.party.dto.partyAdmin.response;

import com.partyguham.common.entity.Status;
import com.partyguham.party.entity.Party;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdatePartyStatusResponseDto {

    private Long id;
    private Status status; // "active" / "archived"

    public static UpdatePartyStatusResponseDto from(Party party) {
        return UpdatePartyStatusResponseDto.builder()
                .id(party.getId())
                .status(party.getStatus())
                .build();
    }
}