package com.partyguham.party.dto.party.response;

import com.partyguham.party.dto.party.PartyTypeDto;
import com.partyguham.party.entity.PartyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyTypeResponse {

    private List<PartyTypeDto> partyTypes;

    public static PartyTypeResponse from(List<PartyType> partyTypes) {
        List<PartyTypeDto> partyTypeDtos = partyTypes.stream()
                .map(partyType -> PartyTypeDto.builder()
                        .id(partyType.getId())
                        .type(partyType.getType())
                        .build())
                .toList();

        return PartyTypeResponse.builder()
                .partyTypes(partyTypeDtos)
                .build();
    }
}
