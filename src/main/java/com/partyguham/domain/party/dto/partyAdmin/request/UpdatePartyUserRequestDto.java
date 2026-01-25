package com.partyguham.domain.party.dto.partyAdmin.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UpdatePartyUserRequestDto {

    /**
     * 변경할 포지션 ID (선택)
     */
    @NotNull(message = "변경할 포지션 ID는 필수 입력값입니다.")
    private Long positionId;
}
