package com.partyguham.party.dto.party.response;


import com.partyguham.party.entity.Party;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyResponseDto {
    private Long id;
    private Long partyTypeId;
    private Long positionId;
    private String title;
    private String content;
    private String image;
    private String status;

    public static PartyResponseDto of(Party party) {
        return PartyResponseDto.builder()
                .id(party.getId())
                .partyTypeId(party.getPartyType().getId())
                .title(party.getTitle())
                .content(party.getContent())
                .image(party.getImage())
                .status(party.getStatus().name())
                .build();
    }
}
