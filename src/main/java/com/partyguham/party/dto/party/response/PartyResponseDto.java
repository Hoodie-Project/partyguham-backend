package com.partyguham.party.dto.party.response;


import com.partyguham.party.entity.Party;
import com.partyguham.party.entity.PartyStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PartyResponseDto {
    private Long id;
    private String title;
    private String content;
    private String image;
    private PartyStatus partyStatus; // PartyStatus enum
    private String createdAt;
    private String updatedAt;


    public static PartyResponseDto from(Party party) {
        return PartyResponseDto.builder()
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
