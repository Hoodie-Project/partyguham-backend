package com.partyguham.party.dto.party.response;


import com.partyguham.party.entity.Party;
import com.partyguham.party.entity.PartyStatus;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PartyResponse {
    private Long id;
    private String title;
    private String content;
    private String image;
    private PartyStatus partyStatus; // PartyStatus enum
    private String createdAt;
    private String updatedAt;


    public static PartyResponse from(Party party) {
        return PartyResponse.builder()
                .id(party.getId())
                .title(party.getTitle())
                .content(party.getContent())
                .image(party.getImage())
                .partyStatus(party.getPartyStatus())
                .createdAt(party.getCreatedAt().toString())
                .updatedAt(party.getUpdatedAt().toString())
                .build();
    }
}
