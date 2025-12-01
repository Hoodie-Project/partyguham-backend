package com.partyguham.party.dto.partyAdmin.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeletePartyUsersBodyRequestDto {

    /**
     * 강제 퇴장시킬 party_user_id 목록
     */
    @NotEmpty
    private List<Long> partyUserIds;
}
