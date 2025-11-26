package com.partyguham.party.core.dto.party.response;

import com.partyguham.party.core.entity.Party;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PartiesDto {

    private Long id;
    private PartyTypeDto partyType;
    private String title;
    private String content;
    private String image;
    private String status;     
    private String createdAt;
    private String updatedAt;
    private Integer recruitmentCount;

    public static PartiesDto from(Party party) {
        return PartiesDto.builder()
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
                .status(party.getStatus().name())
                .createdAt(party.getCreatedAt().toString())
                .updatedAt(party.getUpdatedAt().toString())
                .recruitmentCount(
                        party.getPartyRecruitments() != null ?
                                party.getPartyRecruitments().size() : 0
                )
                .build();
    }
}
