package com.partyguham.party.dto.party.response;

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
public class PartyTypeResponseDto {

    private List<PartyTypeDto> partyTypes;

    public static PartyTypeResponseDto from(List<PartyType> partyTypes) {
        List<PartyTypeDto> partyTypeDtos = partyTypes.stream()
                .map(partyType -> PartyTypeDto.builder()
                        .id(partyType.getId())
                        .type(partyType.getType())
                        .build())
                .toList();

        return PartyTypeResponseDto.builder()
                .partyTypes(partyTypeDtos)
                .build();
    }
}
