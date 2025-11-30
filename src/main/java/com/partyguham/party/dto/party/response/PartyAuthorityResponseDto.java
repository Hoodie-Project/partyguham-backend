package com.partyguham.party.dto.party.response;

import com.partyguham.party.entity.PartyAuthority;
import com.partyguham.party.entity.PartyUser;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyAuthorityResponseDto {

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

    public static PartyAuthorityResponseDto from(PartyUser partyUser) {
        return PartyAuthorityResponseDto.builder()
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
