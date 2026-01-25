package com.partyguham.domain.party.dto.party.response;

import com.partyguham.domain.party.entity.PartyAuthority;
import com.partyguham.domain.party.entity.PartyUser;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyAuthorityResponse {

    private Long id;
    private PartyAuthority authority; // @JsonValue로 자동 직렬화 (master / deputy / member)
    private PositionDto position;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PositionDto {
        private Long id;
        private String main;
        private String sub;
    }

    public static PartyAuthorityResponse from(PartyUser partyUser) {
        return PartyAuthorityResponse.builder()
                .id(partyUser.getId())
                .authority(partyUser.getAuthority())
                .position(
                        partyUser.getPosition() != null ?
                                PositionDto.builder()
                                        .id(partyUser.getPosition().getId())
                                        .main(partyUser.getPosition().getMain())
                                        .sub(partyUser.getPosition().getSub())
                                        .build()
                                : null
                )
                .build();
    }
}
