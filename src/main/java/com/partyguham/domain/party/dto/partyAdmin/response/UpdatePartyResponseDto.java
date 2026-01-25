package com.partyguham.domain.party.dto.partyAdmin.response;

import com.partyguham.domain.party.entity.Party;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdatePartyResponseDto {

    private Long id;
    private String title;
    private String content;
    private Long partyTypeId;
    private String imageKey;

    public static UpdatePartyResponseDto from(Party party) {
        return UpdatePartyResponseDto.builder()
                .id(party.getId())
                .title(party.getTitle())
                .content(party.getContent())
                .partyTypeId(party.getPartyType().getId())
                .imageKey(party.getImage())
                .build();
    }
}