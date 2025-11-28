package com.partyguham.party.service;

import com.partyguham.party.dto.partyAdmin.request.*;
import com.partyguham.party.dto.partyAdmin.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyAdminService {

    public UpdatePartyResponseDto updateParty(Long partyId, Long userId, UpdatePartyRequestDto request) {
        return null;
    }


    public UpdatePartyStatusResponseDto updatePartyStatus(Long partyId,
                                                          Long userId,
                                                          UpdatePartyStatusRequestDto request) {
        return null;
    }


    public void deletePartyImage(Long partyId, Long userId) {

    }


    public void deleteParty(Long partyId, Long userId) {

    }


    public PartyDelegationResponseDto delegateParty(Long partyId, Long userId, PartyDelegationRequestDto request) {
        return null;
    }


    public GetAdminPartyUsersResponseDto getPartyUsers(Long partyId,
                                                       GetAdminPartyUsersRequestDto request,
                                                       Long userId) {
        return null;
    }


    public UpdatePartyUserResponseDto updatePartyUser(Long partyId,
                                                      Long partyUserId,
                                                      Long userId,
                                                      UpdatePartyUserRequestDto request) {
        return null;
    }


    public void deletePartyUser(Long partyId, Long partyUserId, Long userId) {

    }


    public void deletePartyUserBatch(Long partyId, Long userId, DeletePartyUsersBodyRequestDto request) {

    }
}
