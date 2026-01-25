package com.partyguham.domain.party.dto.party.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartyCreateRequest {
    private String title;
    private String content;
    private Long partyTypeId;
    private Long positionId;
}
