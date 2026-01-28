package com.partyguham.domain.party.dto.party.response;


import com.partyguham.domain.party.dto.party.PartiesDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPartiesResponse {

    private long total;
    private List<PartiesDto> parties;

    public static GetPartiesResponse fromPage(long total, List<PartiesDto> parties) {
        return GetPartiesResponse.builder()
                .total(total)
                .parties(parties)
                .build();
    }
}