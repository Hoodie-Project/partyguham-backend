package com.partyguham.party.service;

import com.partyguham.party.dto.party.request.GetPartiesRequestDto;
import com.partyguham.party.dto.party.request.GetPartyUsersRequestDto;
import com.partyguham.party.dto.party.request.PartyCreateRequestDto;
import com.partyguham.party.dto.party.response.*;

public interface PartyService { 
   
    PartyResponseDto createParty(PartyCreateRequestDto request, Long userId); //파티 생성
    
    GetPartiesResponseDto getParties(GetPartiesRequestDto request); //파티 목록 조회
    
    GetPartyResponseDto getParty(Long partyId); // 파티 단일 조회

    GetPartyUserResponseDto getPartyUsers(GetPartyUsersRequestDto request, Long partyId); // 파티원 목록 조회

    PartyAuthorityResponseDto getPartyAuthority(Long partyId, Long userId); //나의 파티 권한 조회
     
    PartyTypeResponseDto getType(); // 파티 타입 목록 조회

    GetSearchResponseDto getSearch(int page, int limit, String titleSearch); //파티/파티 모집공고 통합검색

    void leaveParty(Long partyId, Long userId); // 파티 나가기

    PartyTypeResponseDto getPartyTypes();

    GetSearchResponseDto searchParties(int page, int limit, String titleSearch);
}   