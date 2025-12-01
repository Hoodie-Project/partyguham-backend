package com.partyguham.party.dto.party.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        private java.util.List<PartiesDto> parties;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartyRecruitmentSearchResultDto {
        private Long total;
        private java.util.List<PartyRecruitmentSearchDto> partyRecruitments;
    }
}