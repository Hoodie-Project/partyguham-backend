package com.partyguham.catalog.dto.response;

import com.partyguham.catalog.entity.Position;
import lombok.Builder;

@Builder
public record PositionResponse(
        Long id,
        String main,
        String sub
) {
    public static PositionResponse from(Position p) {
        return PositionResponse.builder()
                .id(p.getId())
                .main(p.getMain())
                .sub(p.getSub())
                .build();
    }
}