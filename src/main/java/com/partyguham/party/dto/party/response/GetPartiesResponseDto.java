package com.partyguham.party.dto.party.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPartiesResponseDto {

    private long total;
    private List<PartiesDto> parties;

    public static GetPartiesResponseDto fromPage(long total, List<PartiesDto> parties) {
        return GetPartiesResponseDto.builder()
                .total(total)
                .parties(parties)
                .build();
    }
}