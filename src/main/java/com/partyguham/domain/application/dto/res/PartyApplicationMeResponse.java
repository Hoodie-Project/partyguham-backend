package com.partyguham.domain.application.dto.res;

import com.partyguham.domain.application.entity.PartyApplication;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PartyApplicationMeResponse {

    private Long id;           // 지원 PK
    private String status;     // pending / approved / rejected ...
    private LocalDateTime createdAt; // 지원 시각
    private String message;    // 내가 쓴 메세지

    public static PartyApplicationMeResponse from(PartyApplication app) {
        return PartyApplicationMeResponse.builder()
                .id(app.getId())
                .status(app.getApplicationStatus().name().toLowerCase())
                .createdAt(app.getCreatedAt())
                .message(app.getMessage())
                .build();
    }
}
