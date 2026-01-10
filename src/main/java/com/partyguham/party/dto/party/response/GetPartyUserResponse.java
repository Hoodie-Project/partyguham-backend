package com.partyguham.party.dto.party.response;

import com.partyguham.party.dto.party.PartyUserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPartyUserResponse {

    private List<PartyUserDto> partyAdmin;
    private List<PartyUserDto> partyUser;

    public static GetPartyUserResponse from(List<PartyUserDto> partyAdmin, List<PartyUserDto> partyUser) {
        return GetPartyUserResponse.builder()
                .partyAdmin(partyAdmin)
                .partyUser(partyUser)
                .build();
    }
}
