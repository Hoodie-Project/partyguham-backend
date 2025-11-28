package com.partyguham.party.dto.party.response;

import com.partyguham.party.entity.Party;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPartyResponseDto {

    private Long id;
    private PartyTypeDto partyType;
    private String title;
    private String content;
    private String image;
    private String status;
    private String createdAt;
    private String updatedAt;
    private Integer userCount;
    private Integer recruitmentCount;

    public static GetPartyResponseDto from(Party party) {
        return GetPartyResponseDto.builder()
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
                .userCount(
                        party.getPartyUsers() != null ?
                                party.getPartyUsers().size() : 0
                )
                .recruitmentCount(
                        party.getPartyRecruitments() != null ?
                                party.getPartyRecruitments().size() : 0
                )
                .build();
    }
}
