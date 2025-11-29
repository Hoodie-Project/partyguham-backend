package com.partyguham.party.core.dto.party.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetSearchResponseDto {

    private PartySearchDto party;
    private PartyRecruitmentSearchResultDto partyRecruitment;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartySearchDto {
        private Long total;
        private List<PartiesDto> parties;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartyRecruitmentSearchResultDto {
        private Long total;
        private List<PartyRecruitmentSearchDto> partyRecruitments;
    }
}
