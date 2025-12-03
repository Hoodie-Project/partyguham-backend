package com.partyguham.application.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePartyApplicationRequestDto {

    /**
     * 지원 메시지
     * (선택사항일 수 있음)
     */
    private String message;
}