package com.partyguham.party.service;

import com.partyguham.party.core.dto.partyAdmin.request.*;
import com.partyguham.party.core.dto.partyAdmin.response.*;
import com.partyguham.party.dto.partyAdmin.request.*;
import com.partyguham.party.dto.partyAdmin.response.*;

public interface PartyAdminService {
    
    UpdatePartyResponseDto updateParty(Long partyId, Long userId, UpdatePartyRequestDto request); // 파티 정보 수정
    
    UpdatePartyStatusResponseDto updatePartyStatus(Long partyId, Long userId, UpdatePartyStatusRequestDto request); // 파티 상태 수정
    
    void deletePartyImage(Long partyId, Long userId); // 파티 이미지 삭제
    
    void deleteParty(Long partyId, Long userId); // 파티 삭제
    
    PartyDelegationResponseDto delegateParty(Long partyId, Long userId, PartyDelegationRequestDto request); // 파티 위임
    
    GetAdminPartyUsersResponseDto getPartyUsers(Long partyId, GetAdminPartyUsersRequestDto request, Long userId); // 파티 사용자 목록 조회
    
    UpdatePartyUserResponseDto updatePartyUser(Long partyId, Long partyUserId, Long userId, UpdatePartyUserRequestDto request); // 파티 사용자 수정
    
    void deletePartyUser(Long partyId, Long partyUserId, Long userId); // 파티 사용자 삭제
    
    void deletePartyUserBatch(Long partyId, Long userId, DeletePartyUsersBodyRequestDto request); // 파티 사용자 일괄 삭제
}
