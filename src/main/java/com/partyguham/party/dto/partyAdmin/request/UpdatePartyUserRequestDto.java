package com.partyguham.party.dto.partyAdmin.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UpdatePartyUserRequestDto {

    /**
     * 변경할 포지션 ID (선택)
     */
    private Long positionId;
}
