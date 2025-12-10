package com.partyguham.party.dto.party.response;

import com.partyguham.party.entity.Party;
import com.partyguham.party.entity.PartyStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartyDto {
    private Long id;
    private String title;
    private String image;
    private PartyStatus partyStatus;
    private PartyTypeDto partyType;

    /**
     * Party 엔티티를 PartyDto로 변환하는 정적 팩토리 메서드
     * (간소화된 버전 - Recruitment 등에서 사용)
     */
    public static PartyDto from(Party party) {
        return PartyDto.builder()
                .id(party.getId())
                .title(party.getTitle())
                .image(party.getImage())
                .partyStatus(party.getPartyStatus())
                .partyType(
                        PartyTypeDto.builder()
                                .id(party.getPartyType().getId())
                                .type(party.getPartyType().getType())
                                .build()
                )
                .build();
    }
}
