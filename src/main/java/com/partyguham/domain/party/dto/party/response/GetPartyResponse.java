package com.partyguham.domain.party.dto.party.response;

import com.partyguham.domain.party.dto.party.PartyTypeDto;
import com.partyguham.domain.party.entity.Party;
import com.partyguham.domain.party.entity.PartyStatus;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPartyResponse {

    private Long id;
    private PartyTypeDto partyType;
    private String title;
    private String content;
    private String image;
    private PartyStatus partyStatus;
    private String createdAt;
    private String updatedAt;

    public static GetPartyResponse from(Party party) {
        return GetPartyResponse.builder()
                .id(party.getId())
                .partyType(
                        PartyTypeDto.builder()
                                .id(party.getPartyType().getId())
                                .type(party.getPartyType().getType())
                                .build()
                )
                .title(party.getTitle())
                .content(party.getContent())
                .image(party.getImage())
                .partyStatus(party.getPartyStatus())
                .createdAt(party.getCreatedAt().toString())
                .updatedAt(party.getUpdatedAt().toString())
                .build();
    }
}
