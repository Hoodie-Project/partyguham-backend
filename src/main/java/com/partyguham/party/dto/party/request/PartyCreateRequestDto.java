package com.partyguham.party.dto.party.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartyCreateRequestDto {
    private String title;
    private String content;
    private Long partyTypeId;
    private Long positionId;
}
