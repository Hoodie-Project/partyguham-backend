package com.partyguham.party.dto.party.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPartyUserResponseDto {

    private List<PartyUserDto> partyAdmin;
    private List<PartyUserDto> partyUser;
}
