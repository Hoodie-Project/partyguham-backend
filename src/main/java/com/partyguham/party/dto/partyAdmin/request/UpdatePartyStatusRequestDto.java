package com.partyguham.party.dto.partyAdmin.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePartyStatusRequestDto {

    /**
     * 파티 상태
     * - "active"   : 진행중
     * - "archived" : 종료
     */
    @NotNull
    @Pattern(
            regexp = "active|archived",
            message = "status는 active 또는 archived만 허용됩니다."
    )
    private String status;
}