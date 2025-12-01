package com.partyguham.party.dto.party.response;


import com.partyguham.party.entity.Party;
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
    private String status;
    private String createdAt;
    private String updatedAt;


    public static PartyResponseDto of(Party party) {
        return PartyResponseDto.builder()
                .id(party.getId())
                .title(party.getTitle())
                .content(party.getContent())
                .image(party.getImage())
                .status(party.getStatus().name())
                .createdAt(party.getCreatedAt().toString())
                .updatedAt(party.getUpdatedAt().toString())
                .build();
    }
}
