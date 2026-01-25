package com.partyguham.domain.party.dto.partyAdmin.response;

import com.partyguham.domain.party.entity.Party;
import com.partyguham.domain.party.entity.PartyUser;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyDelegationResponseDto {

    private Long partyId;
    private Long previousMasterPartyUserId;
    private Long newMasterPartyUserId;

    public static PartyDelegationResponseDto from(Party party, PartyUser previousMaster, PartyUser newMaster) {
        return PartyDelegationResponseDto.builder()
                .partyId(party.getId())
                .previousMasterPartyUserId(previousMaster.getId())
                .newMasterPartyUserId(newMaster.getId())
                .build();
    }
}